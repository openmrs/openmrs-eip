import {Component, ElementRef, OnInit, ViewChild,} from '@angular/core';
import {ConflictService} from "./conflict.service";
import {Conflict} from "./conflict";
import {select, Store} from "@ngrx/store";
import {GET_CONFLICTS, GET_DIFF, GET_RESOLVER_TASK_STATUS, GET_VERIFY_TASK_STATUS} from "./state/conflict.reducer";
import {ConflictsLoaded, ResolverTaskStatusUpdated, VerifyTaskStatusUpdated, ViewDiff} from "./state/conflict.actions";
import {BaseListingComponent} from "../../shared/base-listing.component";
import {NgbModal, NgbModalOptions, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {Subscription} from "rxjs";
import {ConflictTaskStatus} from "./conflict-task-status";
import {Decision} from "./decision.enum";
import {Diff} from "./diff";
import {MessageDialogComponent} from "../../shared/dialogs/message.component";

@Component({
	selector: 'receiver-conflicts',
	templateUrl: './conflict.component.html',
	styleUrls: ['./conflict.component.scss']
})
export class ConflictComponent extends BaseListingComponent implements OnInit {

	count?: number;

	conflicts?: Conflict[];

	diff?: Diff;

	modalRef?: NgbModalRef;

	verifyTaskStatus?: ConflictTaskStatus;

	verifyTaskTimeoutId?: number;

	verifierReloadMillis?: number;

	resolverTaskStatus?: ConflictTaskStatus;

	resolverTaskTimeoutId?: number;

	resolverReloadMillis?: number;

	decision?: Decision;

	propsToSync?: any[];

	@ViewChild('diffTemplate')
	diffRef?: ElementRef;

	diffSubscription?: Subscription;

	loadedSubscription?: Subscription;

	verifyStatusSubscription?: Subscription;

	resolverStatusSubscription?: Subscription;

	constructor(
		private service: ConflictService,
		private store: Store,
		private modalService: NgbModal
	) {
		super();
	}

	ngOnInit(): void {
		this.init();
		this.loadedSubscription = this.store.pipe(select(GET_CONFLICTS)).subscribe(
			countAndItems => {
				this.count = countAndItems.count;
				this.conflicts = countAndItems.items;
				this.reRender();
			}
		);

		this.diffSubscription = this.store.pipe(select(GET_DIFF)).subscribe(
			diff => {
				this.diff = diff;
				this.decision = undefined;
				this.propsToSync = undefined;
				if (this.diff) {
					this.propsToSync = [];
					this.diff?.properties?.forEach(p => {
						this.propsToSync?.push({'name': p, 'checked': false});
					});

					this.showDiffDialog();
				}
			}
		);

		this.verifyStatusSubscription = this.store.pipe(select(GET_VERIFY_TASK_STATUS)).subscribe(status => {
				if (status) {
					let completed = this.verifyTaskStatus?.running && !status.running;
					this.verifyTaskStatus = status;
					if (this.verifyTaskStatus.running) {
						this.getVerifyTaskStatus(5000);
					}

					let millis = status.lastUpdated?.getTime();
					if (!this.verifierReloadMillis) {
						this.verifierReloadMillis = millis;
					}

					//Refresh the conflicts when the task is done or every 30 seconds
					if (completed || (millis && this.verifierReloadMillis && (millis - this.verifierReloadMillis) >= 30000)) {
						this.verifierReloadMillis = millis;
						this.loadConflicts();
					}
				}
			}
		);

		this.resolverStatusSubscription = this.store.pipe(select(GET_RESOLVER_TASK_STATUS)).subscribe(status => {
				if (status) {
					let completed = this.resolverTaskStatus?.running && !status.running;
					this.resolverTaskStatus = status;
					if (this.resolverTaskStatus.running) {
						this.getResolverTaskStatus(5000);
					}

					let millis = status.lastUpdated?.getTime();
					if (!this.resolverReloadMillis) {
						this.resolverReloadMillis = millis;
					}

					//Refresh the conflicts when the task is done or every 30 seconds
					if (completed || (millis && this.resolverReloadMillis && (millis - this.resolverReloadMillis) >= 30000)) {
						this.resolverReloadMillis = millis;
						this.loadConflicts();
					}
				}
			}
		);

		this.loadConflicts();
		if (this.verifyTaskTimeoutId) {
			clearTimeout(this.verifyTaskTimeoutId);
		}

		if (this.resolverTaskTimeoutId) {
			clearTimeout(this.resolverTaskTimeoutId);
		}

		this.getVerifyTaskStatus(0);
	}

	loadConflicts(): void {
		this.service.getConflictCountAndItems().subscribe(countAndItems => {
			this.store.dispatch(new ConflictsLoaded(countAndItems));
		});
	}

	startVerifyTask(): void {
		this.service.startVerifyTask().subscribe(() => {
			this.store.dispatch(new VerifyTaskStatusUpdated(new ConflictTaskStatus(true, new Date())));
		});
	}

	getVerifyTaskStatus(delay: number): void {
		this.verifyTaskTimeoutId = setTimeout(() => {
			this.service.getVerifyTaskStatus().subscribe(running => {
				this.store.dispatch(new VerifyTaskStatusUpdated(new ConflictTaskStatus(running, new Date())));
			});

		}, delay);
	}

	startResolverTask(): void {
		this.service.startResolverTask().subscribe(() => {
			this.store.dispatch(new ResolverTaskStatusUpdated(new ConflictTaskStatus(true, new Date())));
		});
	}

	getResolverTaskStatus(delay: number): void {
		this.resolverTaskTimeoutId = setTimeout(() => {
			this.service.getResolverTaskStatus().subscribe(running => {
				this.store.dispatch(new ResolverTaskStatusUpdated(new ConflictTaskStatus(running, new Date())));
			});

		}, delay);
	}

	resolve(): void {
		let props: any | undefined[] = [];
		if (this.decision == Decision.MERGE) {
			props = this.getPropsToSync();
		}

		let data = {'decision': this.decision, 'propsToSync': props};
		let conflict: Conflict | undefined = this.diff?.conflict;
		this.closeResolutionDialog();
		if (conflict) {
			this.service.resolveConflict(conflict, data).subscribe(
				() => {
					this.showResolutionResultDialog($localize`:@@receiver-conflict-resolve-success:Resolved successfully`, 'success');
				},

				(e) => {
					this.showResolutionResultDialog($localize`:@@receiver-conflict-resolve-fail:An error occurred while resolving the conflict`, 'error');
				}
			);
		}
	}

	viewDiff(conflict: Conflict): void {
		this.service.getDiff(conflict).subscribe(diff => {
			diff.conflict = conflict;
			this.store.dispatch(new ViewDiff(diff));
		});
	}

	showDiffDialog(): void {
		const dialogConfig: NgbModalOptions = {
			backdrop: 'static',
			size: 'xl',
			scrollable: true
		}

		this.modalRef = this.modalService.open(this.diffRef, dialogConfig);
		this.modalRef.closed.subscribe(() => {
			this.store.dispatch(new ViewDiff());
		});
	}

	getPropsToSync(): string[] {
		let props: any | undefined[] = [];
		if (this.decision == Decision.MERGE) {
			props = this.propsToSync?.filter(p => p.checked).map(p => p.name);
		}

		return props;
	}

	getCurrentValue(propertyName: string): any {
		return this.getDisplayValue(propertyName, this.diff?.currentState[propertyName]);
	}

	getNewValue(propertyName: string): any {
		return this.getDisplayValue(propertyName, this.diff?.newState[propertyName]);
	}

	getDisplayValue(propertyName: string, value: any): any {
		if (propertyName.endsWith('Uuid') && value) {
			let originalValue: string = value;
			return originalValue.substring(originalValue.indexOf('(') + 1, originalValue.indexOf(')'));
		}

		return value;
	}

	isResolutionFormValid(): boolean {
		if (this.decision != null) {
			if (this.decision != Decision.MERGE) {
				return true;
			}

			return this.getPropsToSync().length > 0;
		}

		return false;
	}

	showResolutionResultDialog(msg: string, theme: string): void {
		const dialogConfig: NgbModalOptions = {
			backdrop: 'static'
		}

		const dialogRef = this.modalService.open(MessageDialogComponent, dialogConfig);
		dialogRef.componentInstance.theme = '-' + theme;
		dialogRef.componentInstance.message = msg;

		dialogRef.closed.subscribe(() => {
			this.loadConflicts();
		});
	}

	closeResolutionDialog(): void {
		this.modalRef?.close();
	}

	ngOnDestroy(): void {
		this.diffSubscription?.unsubscribe();
		this.loadedSubscription?.unsubscribe();
		this.verifyStatusSubscription?.unsubscribe();
		this.resolverStatusSubscription?.unsubscribe();
		clearTimeout(this.verifyTaskTimeoutId);
		clearTimeout(this.resolverTaskTimeoutId);
		super.ngOnDestroy();
	}

}
