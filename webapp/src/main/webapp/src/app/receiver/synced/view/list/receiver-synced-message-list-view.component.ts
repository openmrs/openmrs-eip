import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {NgbModal, NgbModalOptions, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {Subscription} from "rxjs";
import {select, Store} from "@ngrx/store";
import {GET_SYNCED_MSGS, MSG_TO_VIEW} from "../../state/synced-message.reducer";
import {SyncedMessagesLoaded, ViewSyncedMessage} from "../../state/synced-message.actions";
import {BaseListingComponent} from "../../../../shared/base-listing.component";
import {ReceiverSyncedMessage} from "../../receiver-synced-message";
import {ReceiverSyncedMessageService} from "../../receiver-synced-message.service";

@Component({
	selector: 'receiver-synced-msg-list-view',
	templateUrl: './receiver-synced-message-list-view.component.html'
})
export class ReceiverSyncedMessageListViewComponent extends BaseListingComponent implements OnInit {

	syncedMessages?: ReceiverSyncedMessage[];

	msgToView?: ReceiverSyncedMessage;

	modalRef?: NgbModalRef;

	parsedEntityPayLoad?: any;

	@ViewChild('detailsTemplate')
	detailsRef?: ElementRef;

	viewSubscription?: Subscription;

	loadedSubscription?: Subscription;

	constructor(
		private service: ReceiverSyncedMessageService,
		private store: Store,
		private modalService: NgbModal
	) {
		super();
	}

	ngOnInit(): void {
		this.init();
		this.loadedSubscription = this.store.pipe(select(GET_SYNCED_MSGS)).subscribe(
			syncedItems => {
				this.syncedMessages = syncedItems;
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

		this.loadSyncedMessages();
	}

	loadSyncedMessages(): void {
		this.service.getSyncedMessageCountAndItems().subscribe(countAndItems => {
			this.store.dispatch(new SyncedMessagesLoaded(countAndItems));
		});
	}

	viewSyncedMessage(syncedMessage: ReceiverSyncedMessage): void {
		this.store.dispatch(new ViewSyncedMessage(syncedMessage));
	}

	showDetailsDialog(): void {
		const dialogConfig: NgbModalOptions = {
			size: 'xl',
			scrollable: true
		}

		this.modalRef = this.modalService.open(this.detailsRef, dialogConfig);
		this.modalRef.closed.subscribe(() => {
			this.store.dispatch(new ViewSyncedMessage());
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
