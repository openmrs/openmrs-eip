import {Component, OnInit} from '@angular/core';
import {BaseListingComponent} from "../../shared/base-listing.component";
import {Subscription} from "rxjs";
import {select, Store} from "@ngrx/store";
import {SenderSyncMessage} from "./sender-sync-message";
import {SenderSyncMessageService} from "./sender-sync-message.service";
import {GET_MSGS} from "./state/sender-sync-message.reducer";
import {SenderSyncMessagesLoaded} from "./state/sender-sync-message.actions";

@Component({
	selector: 'sender-sync-messages',
	templateUrl: './sender-sync-message.component.html'
})
export class SenderSyncMessageComponent extends BaseListingComponent implements OnInit {

	count?: number;

	syncMessages?: SenderSyncMessage[];

	loadedSubscription?: Subscription;

	constructor(
		private service: SenderSyncMessageService,
		private store: Store
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

		this.loadSyncMessages();
	}

	loadSyncMessages(): void {
		this.service.getMessageCountAndItems().subscribe(countAndItems => {
			this.store.dispatch(new SenderSyncMessagesLoaded(countAndItems));
		});
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
		super.ngOnDestroy();
	}

}

