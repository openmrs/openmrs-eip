import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {BaseListingComponent} from "../../shared/base-listing.component";
import {NgbModal, NgbModalOptions, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {Subscription} from "rxjs";
import {ReceiverSyncMessage} from "./receiver-sync-message";
import {select, Store} from "@ngrx/store";
import {GET_MSGS, MSG_TO_VIEW} from "./state/sync-message.reducer";
import {SyncMessagesLoaded, ViewSyncMessage} from "./state/sync-message.actions";
import {ReceiverSyncMessageService} from "./receiver-sync-message.service";

@Component({
	selector: 'receiver-sync-messages',
	templateUrl: './receiver-sync-message.component.html'
})
export class ReceiverSyncMessageComponent extends BaseListingComponent implements OnInit {

	count?: number;

	syncMessages?: ReceiverSyncMessage[];

	msgToView?: ReceiverSyncMessage;

	modalRef?: NgbModalRef;

	parsedEntityPayLoad?: any;

	@ViewChild('detailsTemplate')
	detailsRef?: ElementRef;

	viewSubscription?: Subscription;

	loadedSubscription?: Subscription;

	constructor(
		private service: ReceiverSyncMessageService,
		private store: Store,
		private modalService: NgbModal
	) {
		super();
	}

	ngOnInit(): void {
		this.init();
		this.loadedSubscription = this.store.pipe(select(GET_MSGS)).subscribe(
			countAndItems => {
				this.count = countAndItems.count;
				this.syncMessages = countAndItems.items;
				this.reRender();
			}
		);

		this.viewSubscription = this.store.pipe(select(MSG_TO_VIEW)).subscribe(
			msg => {
				this.msgToView = msg;
				if (this.msgToView) {
					if (this.msgToView.entityPayload) {
						this.parsedEntityPayLoad = JSON.parse(this.msgToView.entityPayload);
					}

					this.showDetailsDialog();
				}
			}
		);

		this.loadSyncMessages();
	}

	loadSyncMessages(): void {
		this.service.getSyncMessageCountAndItems().subscribe(countAndItems => {
			this.store.dispatch(new SyncMessagesLoaded(countAndItems));
		});
	}

	viewSyncMessage(syncMessage: ReceiverSyncMessage): void {
		this.store.dispatch(new ViewSyncMessage(syncMessage));
	}

	showDetailsDialog(): void {
		const dialogConfig: NgbModalOptions = {
			size: 'xl',
			scrollable: true
		}

		this.modalRef = this.modalService.open(this.detailsRef, dialogConfig);
		this.modalRef.closed.subscribe(() => {
			this.store.dispatch(new ViewSyncMessage());
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
