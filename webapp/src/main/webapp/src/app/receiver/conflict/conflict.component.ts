import {Component, ElementRef, OnInit, ViewChild,} from '@angular/core';
import {ConflictService} from "./conflict.service";
import {Conflict} from "./conflict";
import {select, Store} from "@ngrx/store";
import {CONFLICT_TO_VIEW, GET_CONFLICTS} from "./state/conflict.reducer";
import {ConflictsLoaded, ViewConflict} from "./state/conflict.actions";
import {BaseListingComponent} from "../../shared/base-listing.component";
import {NgbModal, NgbModalOptions, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ConfirmDialogComponent} from "../../shared/dialogs/confirm.component";
import {Subscription} from "rxjs";

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

	toCleanCount: number = 0;

	cleanedCount: number = 0;

	@ViewChild('detailsTemplate')
	detailsRef?: ElementRef;

	@ViewChild('verifyConflictsTemplate')
	verifyDetailsRef?: ElementRef;

	@ViewChild('cleanedConflictsTemplate')
	cleanedDetailsRef?: ElementRef;

	viewSubscription?: Subscription;

	loadedSubscription?: Subscription;

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

		this.loadConflicts();
	}

	loadConflicts(): void {
		this.service.getConflictCountAndItems().subscribe(countAndItems => {
			this.store.dispatch(new ConflictsLoaded(countAndItems));
		});
	}

	verifyConflicts(): void {
		this.service.verifyConflicts().subscribe(count => {
			this.toCleanCount = count;
			if(this.toCleanCount > 0) {
				this.showVerifyDialog();
			}
		});
	}

	cleanFalseConflicts(): void {
		this.closeDetailsDialog();
		this.service.cleanConflicts().subscribe(count => {
			this.cleanedCount = count;
			this.showCleanDialog();
		});
	}

	confirmDialog(conflict: Conflict): void {
		const dialogConfig: NgbModalOptions = {
			backdrop: 'static',
		}

		const modelRef = this.modalService.open(ConfirmDialogComponent, dialogConfig);
		modelRef.componentInstance.title = $localize`:@@receiver-conflict-confirm-resolve-title:Confirm Resolution`;
		modelRef.componentInstance.message = $localize`:@@receiver-conflict-confirm-resolve-message:Are you sure you want to mark the conflict as resolved?`;

		modelRef.closed.subscribe(() => {
			this.service.updateConflict({...conflict, resolved: true}).subscribe(
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
		super.ngOnDestroy();
	}

	getSimpleClassName(className?: string) {
		return className?.substring(className.lastIndexOf('.') + 1, className.lastIndexOf('Model'));
	}

	showVerifyDialog(): void {
		const dialogConfig: NgbModalOptions = {
			backdrop: 'static',
		}

		this.modalRef = this.modalService.open(this.verifyDetailsRef, dialogConfig);
	}

	showCleanDialog(): void {
		const dialogConfig: NgbModalOptions = {
			backdrop: 'static',
		}

		this.modalRef = this.modalService.open(this.cleanedDetailsRef, dialogConfig);
	}

}
