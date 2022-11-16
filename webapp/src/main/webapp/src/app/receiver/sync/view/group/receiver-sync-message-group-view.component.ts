import {Component} from '@angular/core';
import {ReceiverSyncMessageService} from "../../receiver-sync-message.service";
import {Action, DefaultProjectorFn, MemoizedSelector, Store} from "@ngrx/store";
import {ModelClassPipe} from "../../../../shared/pipes/model-class.pipe";
import {BaseReceiverGroupViewComponent} from "../../../shared/base-receiver-group-view.component";
import {TotalCountAndGroupedItems} from "../../../../shared/total-count-and-grouped-items";
import {GET_SYNC_MSG_GRP_PROP_COUNT_MAP} from "../../state/sync-message.reducer";
import {Observable} from "rxjs";
import {GroupedSyncMessagesLoaded} from "../../state/sync-message.actions";

@Component({
	selector: 'receiver-sync-msg-group-view',
	templateUrl: './receiver-sync-message-group-view.component.html'
})
export class ReceiverSyncMessageGroupViewComponent extends BaseReceiverGroupViewComponent {

	constructor(private service: ReceiverSyncMessageService, store: Store, classPipe: ModelClassPipe) {
		super(store, classPipe)
	}

	getTotalCountAndGroupedItems(groupProperty: string): Observable<TotalCountAndGroupedItems> {
		return this.service.getTotalCountAndGroupedSyncMessages(groupProperty);
	}

	getSelector(): MemoizedSelector<object, Map<string, number> | undefined, DefaultProjectorFn<Map<string, number> | undefined>> {
		return GET_SYNC_MSG_GRP_PROP_COUNT_MAP;
	}

	createLoadAction(countAndGroupedItems: TotalCountAndGroupedItems): Action {
		return new GroupedSyncMessagesLoaded(countAndGroupedItems);
	}

}
