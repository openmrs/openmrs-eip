import {Component, ElementRef, OnInit, ViewChild,} from '@angular/core';
import {ConflictService} from "./conflict.service";
import {Conflict} from "./conflict";
import {select, Store} from "@ngrx/store";
import {GET_CONFLICTS, GET_DIFF, GET_VERIFY_TASK_STATUS} from "./state/conflict.reducer";
import {ConflictsLoaded, VerifyTaskStatusUpdated, ViewDiff} from "./state/conflict.actions";
import {BaseListingComponent} from "../../shared/base-listing.component";
import {NgbModal, NgbModalOptions, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ConfirmDialogComponent} from "../../shared/dialogs/confirm.component";
import {Subscription} from "rxjs";
import {VerifyTaskStatus} from "./verify-task-status";
import {Decision} from "./decision.enum";
import {Diff} from "./diff";

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

	verifyTaskStatus?: VerifyTaskStatus;

	verifyTaskTimeoutId?: number;

	lastReloadMillis?: number;

	@ViewChild('diffTemplate')
	diffRef?: ElementRef;

	diffSubscription?: Subscription;

	loadedSubscription?: Subscription;

	verifyStatusSubscription?: Subscription;

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
				if (this.diff) {
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
					if (!this.lastReloadMillis) {
						this.lastReloadMillis = millis;
					}

					//Refresh the conflicts when the task is done or every 30 seconds
					if (completed || (millis && this.lastReloadMillis && (millis - this.lastReloadMillis) >= 30000)) {
						this.lastReloadMillis = millis;
						this.loadConflicts();
					}
				}
			}
		);

		this.loadConflicts();
		if (this.verifyTaskTimeoutId) {
			clearTimeout(this.verifyTaskTimeoutId);
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
			this.store.dispatch(new VerifyTaskStatusUpdated(new VerifyTaskStatus(true, new Date())));
		});
	}

	getVerifyTaskStatus(delay: number): void {
		this.verifyTaskTimeoutId = setTimeout(() => {
			this.service.getVerifyTaskStatus().subscribe(running => {
				this.store.dispatch(new VerifyTaskStatusUpdated(new VerifyTaskStatus(running, new Date())));
			});

		}, delay);
	}

	confirmDialog(conflict: Conflict): void {
		const dialogConfig: NgbModalOptions = {
			backdrop: 'static',
		}

		const modelRef = this.modalService.open(ConfirmDialogComponent, dialogConfig);
		modelRef.componentInstance.title = $localize`:@@receiver-conflict-confirm-resolve-title:Confirm Resolution`;
		modelRef.componentInstance.message = $localize`:@@receiver-conflict-confirm-resolve-message:Are you sure you want to mark the conflict as resolved?`;

		modelRef.closed.subscribe(() => {
			let data = {'decision': Decision.SYNC_NEW, 'propsToSync': []};
			this.service.resolveConflict(conflict, data).subscribe(
				() => {
					this.loadConflicts();
				}
			);
		});
	}

	viewDiff(conflict: Conflict): void {
		this.service.getDiff(conflict).subscribe(diff => {
			this.store.dispatch(new ViewDiff(diff));
		});
	}

	showDiffDialog(): void {
		const dialogConfig: NgbModalOptions = {
			size: 'xl',
			scrollable: true
		}

		this.modalRef = this.modalService.open(this.diffRef, dialogConfig);
		this.modalRef.closed.subscribe(() => {
			this.store.dispatch(new ViewDiff());
		});
	}

	closeDetailsDialog(): void {
		this.modalRef?.close();
	}

	ngOnDestroy(): void {
		this.diffSubscription?.unsubscribe();
		this.loadedSubscription?.unsubscribe();
		this.verifyStatusSubscription?.unsubscribe();
		clearTimeout(this.verifyTaskTimeoutId);
		super.ngOnDestroy();
	}

}
