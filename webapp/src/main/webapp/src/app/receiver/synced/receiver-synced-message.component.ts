import {Component} from '@angular/core';
import {DefaultProjectorFn, MemoizedSelector, Store} from "@ngrx/store";
import {ViewInfo} from "../shared/view-info";
import {BaseReceiverMultipleViewComponent} from "../shared/base-receiver-multiple-view.component";
import {GET_SYNCED_MSG_TOTAL_COUNT, GET_SYNCED_MSG_VIEW} from "./state/synced-message.reducer";
import {ChangeSyncedMessageView} from "./state/synced-message.actions";

@Component({
	selector: 'receiver-synced-messages',
	templateUrl: './receiver-synced-message.component.html'
})
export class ReceiverSyncedMessageComponent extends BaseReceiverMultipleViewComponent {

	constructor(store: Store) {
		super(store);
	}

	getViewTotalCountSelector(): MemoizedSelector<object, number | undefined, DefaultProjectorFn<number | undefined>> {
		return GET_SYNCED_MSG_TOTAL_COUNT;
	}

	getViewSelector(): MemoizedSelector<object, ViewInfo | undefined, DefaultProjectorFn<ViewInfo | undefined>> {
		return GET_SYNCED_MSG_VIEW;
	}

	createChangeViewAction(viewInfo: ViewInfo): ChangeSyncedMessageView {
		return new ChangeSyncedMessageView(viewInfo);
	}

}
