import {Component, OnInit} from '@angular/core';
import {ReceiverSyncMessageService} from "../../receiver-sync-message.service";
import {select, Store} from "@ngrx/store";
import {GET_SITE_COUNT_MAP} from "../../state/sync-message.reducer";
import {Subscription} from "rxjs";
import {SyncMessagesGroupedBySiteLoaded} from "../../state/sync-message.actions";

@Component({
	selector: 'receiver-sync-msg-site-view',
	templateUrl: './receiver-sync-message-site-view.component.html'
})
export class ReceiverSyncMessageSiteViewComponent implements OnInit {

	columnLabel = $localize`:@@common-health-facility:Health Facility`;

	siteCountMap?: Map<string, number>;

	loadedSubscription?: Subscription;

	constructor(private service: ReceiverSyncMessageService, private store: Store) {
	}

	ngOnInit(): void {
		this.loadedSubscription = this.store.pipe(select(GET_SITE_COUNT_MAP)).subscribe(
			map => {
				this.siteCountMap = map;
			}
		);

		this.service.getTotalCountAndSyncMessagesGroupedBySite("site").subscribe(countAndGroupedItems => {
			this.store.dispatch(new SyncMessagesGroupedBySiteLoaded(countAndGroupedItems));
		});
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
	}

}
