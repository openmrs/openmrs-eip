import {Component, Input, OnInit} from '@angular/core';
import {ReceiverSyncMessageService} from "../../receiver-sync-message.service";
import {select, Store} from "@ngrx/store";
import {Subscription} from "rxjs";
import {ViewInfo} from "../../../shared/view-info";
import {GroupedSyncMessagesLoaded} from "../../state/sync-message.actions";
import {GET_GRP_PROP_COUNT_MAP} from "../../state/sync-message.reducer";
import {View} from "../../../shared/view.enum";

@Component({
	selector: 'receiver-sync-msg-group-view',
	templateUrl: './receiver-sync-message-group-view.component.html'
})
export class ReceiverSyncMessageGroupViewComponent implements OnInit {

	@Input()
	viewInfo?: ViewInfo;

	groupPropertyCountMap?: Map<string, number>;

	loadedSubscription?: Subscription;

	constructor(private service: ReceiverSyncMessageService, private store: Store) {
	}

	ngOnInit(): void {
		this.loadedSubscription = this.store.pipe(select(GET_GRP_PROP_COUNT_MAP)).subscribe(
			map => {
				this.groupPropertyCountMap = map;
			}
		);

		let groupBy: string = 'site';
		if (this.viewInfo?.view == View.ENTITY) {
			groupBy = 'modelClassName';
		}

		this.service.getTotalCountAndGroupedSyncMessages(groupBy).subscribe(countAndGroupedItems => {
			this.store.dispatch(new GroupedSyncMessagesLoaded(countAndGroupedItems));
		});
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
	}

}
