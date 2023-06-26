import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {NgbModal, NgbModalOptions, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {Subscription} from "rxjs";
import {select, Store} from "@ngrx/store";
import {GET_SYNCED_MSGS, MSG_TO_VIEW} from "../../state/synced-message.reducer";
import {SyncedMessagesLoaded, ViewSyncedMessage} from "../../state/synced-message.actions";
import {BaseListingComponent} from "../../../../shared/base-listing.component";
import {ReceiverSyncedMessage} from "../../receiver-synced-message";
import {ReceiverSyncedMessageService} from "../../receiver-synced-message.service";
import {Outcome} from "../../outcome.enum";

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

	responseSentLabel?: string;

	evictedLabel?: string;

	indexedLabel?: string;

	yesLabel?: string;

	noLabel?: string;

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
		this.responseSentLabel = $localize`:@@receiver-response-sent:Response sent`;
		this.evictedLabel = $localize`:@@receiver-evicted-from-cache:Evicted from cache`;
		this.indexedLabel = $localize`:@@receiver-search-index-updated:Search index updated`;
		this.yesLabel = $localize`:@@common-yes:Yes`;
		this.noLabel = $localize`:@@common-no:No`;

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

	getPostSyncStatus(msg: ReceiverSyncedMessage): string {
		let statusMsg = this.responseSentLabel + '=' + (msg.responseSent ? this.yesLabel : this.noLabel);
		if (msg.outcome == Outcome.SUCCESS) {
			if (msg.cached) {
				statusMsg += (', ' + this.evictedLabel + '=' + (msg.evictedFromCache ? this.yesLabel : this.noLabel));
			}
			if (msg.indexed) {
				statusMsg += (', ' + this.indexedLabel + '=' + (msg.searchIndexUpdated ? this.yesLabel : this.noLabel));
			}
		}

		return statusMsg;
	}

	ngOnDestroy(): void {
		this.viewSubscription?.unsubscribe();
		this.loadedSubscription?.unsubscribe();
		super.ngOnDestroy();
	}

}
