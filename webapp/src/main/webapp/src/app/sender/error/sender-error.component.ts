import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {BaseListingComponent} from "../../shared/base-listing.component";
import {select, Store} from "@ngrx/store";
import {SenderErrorService} from "./sender-error.service";
import {SenderError} from "./sender-error";
import {GET_SENDER_ERRORS, SENDER_ERROR_TO_VIEW} from "./state/error.reducer";
import {NgbModal, NgbModalOptions, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ConfirmDialogComponent} from "../../shared/dialogs/confirm.component";
import {SenderErrorsLoaded, ViewSenderError} from "./state/error.actions";
import {Subscription} from "rxjs";

@Component({
	selector: 'sender-errors',
	templateUrl: './sender-error.component.html',
	styleUrls: ['./sender-error.component.scss']
})
export class SenderErrorComponent extends BaseListingComponent implements OnInit {

	count?: number;

	errors?: SenderError[];

	errorToView?: SenderError;

	modalRef?: NgbModalRef;

	parsedEntityPayLoad?: Object;

	@ViewChild('detailsTemplate')
	detailsRef?: ElementRef;

	viewSubscription?: Subscription;

	loadedSubscription?: Subscription;

	constructor(
		private service: SenderErrorService,
		private store: Store,
		private modalService: NgbModal
	) {
		super();
	}

	ngOnInit(): void {
		this.init();
		this.loadedSubscription = this.store.pipe(select(GET_SENDER_ERRORS)).subscribe(
			countAndItems => {
				this.count = countAndItems.count;
				this.errors = countAndItems.items;
				this.reRender();
			}
		);

		this.viewSubscription = this.store.pipe(select(SENDER_ERROR_TO_VIEW)).subscribe(
			error => {
				this.errorToView = error;
				if (this.errorToView) {
					this.showDetailsDialog();
				}
			}
		);

		this.loadErrors();
	}

	loadErrors(): void {
		this.service.getErrorCountAndItems().subscribe(countAndItems => {
			this.store.dispatch(new SenderErrorsLoaded(countAndItems));
		});
	}

	viewSenderError(error: SenderError): void {
		this.store.dispatch(new ViewSenderError(error));
	}

	confirmDialog(error: SenderError): void {
		const dialogConfig: NgbModalOptions = {
			backdrop: 'static',
		}

		const modelRef = this.modalService.open(ConfirmDialogComponent, dialogConfig);
		modelRef.componentInstance.title = $localize`:@@sender-error-confirm-remove-title:Confirm Removal`;
		modelRef.componentInstance.message = $localize`:@@sender-error-confirm-remove-message:Are you sure you want to remove the error from the queue?`;

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
			this.store.dispatch(new ViewSenderError());
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
