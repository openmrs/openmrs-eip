import {Component} from '@angular/core';
import {DefaultProjectorFn, MemoizedSelector, Store} from "@ngrx/store";
import {ViewInfo} from "../shared/view-info";
import {BaseReceiverMultipleViewComponent} from "../shared/base-receiver-multiple-view.component";
import {GET_SYNC_MSG_TOTAL_COUNT, GET_SYNC_MSG_VIEW} from "./state/sync-message.reducer";
import {ChangeSyncMessageView} from "./state/sync-message.actions";

@Component({
	selector: 'receiver-sync-messages',
	templateUrl: './receiver-sync-message.component.html'
})
export class ReceiverSyncMessageComponent extends BaseReceiverMultipleViewComponent {

	constructor(store: Store) {
		super(store);
	}

	getViewTotalCountSelector(): MemoizedSelector<object, number | undefined, DefaultProjectorFn<number | undefined>> {
		return GET_SYNC_MSG_TOTAL_COUNT;
	}

	getViewSelector(): MemoizedSelector<object, ViewInfo | undefined, DefaultProjectorFn<ViewInfo | undefined>> {
		return GET_SYNC_MSG_VIEW;
	}

	createChangeViewAction(viewInfo: ViewInfo): ChangeSyncMessageView {
		return new ChangeSyncMessageView(viewInfo);
	}

}
