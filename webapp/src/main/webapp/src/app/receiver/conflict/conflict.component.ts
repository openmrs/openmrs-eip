import {Component, ElementRef, OnInit, ViewChild,} from '@angular/core';
import {ConflictService} from "./conflict.service";
import {Conflict} from "./conflict";
import {select, Store} from "@ngrx/store";
import {CONFLICT_TO_VIEW, GET_CONFLICTS, GET_VERIFY_TASK_STATUS} from "./state/conflict.reducer";
import {ConflictsLoaded, VerifyTaskStatusUpdated, ViewConflict} from "./state/conflict.actions";
import {BaseListingComponent} from "../../shared/base-listing.component";
import {NgbModal, NgbModalOptions, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ConfirmDialogComponent} from "../../shared/dialogs/confirm.component";
import {Subscription} from "rxjs";
import {VerifyTaskStatus} from "./verify-task-status";

@Component({
	selector: 'receiver-conflicts',
	templateUrl: './conflict.component.html',
	styleUrls: ['./conflict.component.scss']
})
export class ConflictComponent extends BaseListingComponent implements OnInit {

	count?: number;

	conflicts?: Conflict[];

	conflictToView?: Conflict;

	modalRef?: NgbModalRef;

	parsedEntityPayLoad?: any;

	verifyTaskStatus?: VerifyTaskStatus;

	verifyTaskTimeoutId?: number;

	lastReloadMillis?: number;

	@ViewChild('detailsTemplate')
	detailsRef?: ElementRef;

	viewSubscription?: Subscription;

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

		this.viewSubscription = this.store.pipe(select(CONFLICT_TO_VIEW)).subscribe(
			conflict => {
				this.conflictToView = conflict;
				if (this.conflictToView) {
					if (this.conflictToView.entityPayload) {
						this.parsedEntityPayLoad = JSON.parse(this.conflictToView.entityPayload);
					}

					this.showDetailsDialog();
				}
			}
		);

		this.verifyStatusSubscription = this.store.pipe(select(GET_VERIFY_TASK_STATUS)).subscribe(status => {
				if (status == undefined) {
					this.getVerifyTaskStatus(0);
				} else {
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
			this.service.deleteConflict(conflict).subscribe(
				() => {
					this.loadConflicts();
				}
			);
		});
	}

	viewConflict(conflict: Conflict): void {
		this.store.dispatch(new ViewConflict(conflict));
	}

	showDetailsDialog(): void {
		const dialogConfig: NgbModalOptions = {
			size: 'xl',
			scrollable: true
		}

		this.modalRef = this.modalService.open(this.detailsRef, dialogConfig);
		this.modalRef.closed.subscribe(() => {
			this.store.dispatch(new ViewConflict());
		});
	}

	closeDetailsDialog(): void {
		this.modalRef?.close();
	}

	ngOnDestroy(): void {
		this.viewSubscription?.unsubscribe();
		this.loadedSubscription?.unsubscribe();
		this.verifyStatusSubscription?.unsubscribe();
		clearTimeout(this.verifyTaskTimeoutId);
		super.ngOnDestroy();
	}

	getSimpleClassName(className?: string) {
		return className?.substring(className.lastIndexOf('.') + 1, className.lastIndexOf('Model'));
	}

}
