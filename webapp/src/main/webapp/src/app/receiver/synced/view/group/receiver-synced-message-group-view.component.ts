import {Component} from '@angular/core';
import {ReceiverSyncedMessageService} from "../../receiver-synced-message.service";
import {Action, DefaultProjectorFn, MemoizedSelector, Store} from "@ngrx/store";
import {ModelClassPipe} from "../../../../shared/pipes/model-class.pipe";
import {BaseReceiverGroupViewComponent} from "../../../shared/base-receiver-group-view.component";
import {TotalCountAndGroupedItems} from "../../../../shared/total-count-and-grouped-items";
import {GET_SYNCED_MSG_GRP_PROP_COUNT_MAP} from "../../state/synced-message.reducer";
import {Observable} from "rxjs";
import {GroupedSyncedMessagesLoaded} from "../../state/synced-message.actions";

@Component({
	selector: 'receiver-synced-msg-group-view',
	templateUrl: './receiver-synced-message-group-view.component.html'
})
export class ReceiverSyncedMessageGroupViewComponent extends BaseReceiverGroupViewComponent {

	constructor(private service: ReceiverSyncedMessageService, store: Store, classPipe: ModelClassPipe) {
		super(store, classPipe)
	}

	getTotalCountAndGroupedItems(groupProperty: string): Observable<TotalCountAndGroupedItems> {
		return this.service.getTotalCountAndGroupedSyncedMessages(groupProperty);
	}

	getSelector(): MemoizedSelector<object, Map<string, number> | undefined, DefaultProjectorFn<Map<string, number> | undefined>> {
		return GET_SYNCED_MSG_GRP_PROP_COUNT_MAP;
	}

	createLoadAction(countAndGroupedItems: TotalCountAndGroupedItems): Action {
		return new GroupedSyncedMessagesLoaded(countAndGroupedItems);
	}

}
