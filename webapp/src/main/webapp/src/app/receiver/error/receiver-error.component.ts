import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {select, Store} from "@ngrx/store";
import {BaseListingComponent} from "../../shared/base-listing.component";
import {ReceiverError} from "./receiver-error";
import {ReceiverErrorService} from "./receiver-error.service";
import {GET_RECEIVER_ERRORS, RECEIVER_ERROR_TO_VIEW} from "./state/error.reducer";
import {NgbModal, NgbModalOptions, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ConfirmDialogComponent} from "../../shared/dialogs/confirm.component";
import {ReceiverErrorsLoaded, ViewReceiverError} from "./state/error.actions";
import {Subscription} from "rxjs";

@Component({
	selector: 'receiver-errors',
	templateUrl: './receiver-error.component.html',
	styleUrls: ['./receiver-error.component.scss']
})
export class ReceiverErrorComponent extends BaseListingComponent implements OnInit {

	count?: number;

	errors?: ReceiverError[];

	errorToView?: ReceiverError;

	modalRef?: NgbModalRef;

	parsedEntityPayLoad?: any;

	@ViewChild('detailsTemplate')
	detailsRef?: ElementRef;

	viewSubscription?: Subscription;

	loadedSubscription?: Subscription;

	constructor(
		private service: ReceiverErrorService,
		private store: Store,
		private modalService: NgbModal
	) {
		super();
	}

	ngOnInit(): void {
		this.init();
		this.loadedSubscription = this.store.pipe(select(GET_RECEIVER_ERRORS)).subscribe(
			countAndItems => {
				this.count = countAndItems.count;
				this.errors = countAndItems.items;
				this.reRender();
			}
		);

		this.viewSubscription = this.store.pipe(select(RECEIVER_ERROR_TO_VIEW)).subscribe(
			error => {
				this.errorToView = error;
				if (this.errorToView) {
					if (this.errorToView.entityPayload) {
						this.parsedEntityPayLoad = JSON.parse(this.errorToView.entityPayload);
					}

					this.showDetailsDialog();
				}
			}
		);

		this.loadErrors();
	}

	loadErrors(): void {
		this.service.getErrorCountAndItems().subscribe(countAndItems => {
			this.store.dispatch(new ReceiverErrorsLoaded(countAndItems));
		});
	}

	viewError(error: ReceiverError): void {
		this.store.dispatch(new ViewReceiverError(error));
	}

	confirmDialog(error: ReceiverError): void {
		const dialogConfig: NgbModalOptions = {
			backdrop: 'static',
		}

		const modelRef = this.modalService.open(ConfirmDialogComponent, dialogConfig);
		modelRef.componentInstance.title = $localize`:@@receiver-error-confirm-remove-title:Confirm Removal`;
		modelRef.componentInstance.message = $localize`:@@receiver-error-confirm-remove-message:Are you sure you want to remove the error from the queue?`;

		modelRef.closed.subscribe(() => {
			this.service.removeFromQueue(error).subscribe(
				() => {
					this.loadErrors();
				}
			);
		});
	}

	showDetailsDialog(): void {
		const dialogConfig: NgbModalOptions = {
			size: 'xl',
			scrollable: true
		}

		this.modalRef = this.modalService.open(this.detailsRef, dialogConfig);
		this.modalRef.closed.subscribe(() => {
			this.store.dispatch(new ViewReceiverError());
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

}
