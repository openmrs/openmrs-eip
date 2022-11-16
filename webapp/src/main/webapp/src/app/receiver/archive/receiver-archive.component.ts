import {Component} from '@angular/core';
import {DefaultProjectorFn, MemoizedSelector, Store} from '@ngrx/store';
import {ViewInfo} from "../shared/view-info";
import {GET_ARCHIVE_TOTAL_COUNT, GET_ARCHIVE_VIEW} from "./state/receiver-archive.reducer";
import {BaseReceiverMultipleViewComponent} from "../shared/base-receiver-multiple-view.component";
import {ChangeArchivesView} from "./state/receiver-archive.actions";

@Component({
	selector: 'receiver-archives',
	templateUrl: './receiver-archive.component.html',
})
export class ReceiverArchiveComponent extends BaseReceiverMultipleViewComponent {

	constructor(store: Store) {
		super(store);
	}

	getViewTotalCountSelector(): MemoizedSelector<object, number | undefined, DefaultProjectorFn<number | undefined>> {
		return GET_ARCHIVE_TOTAL_COUNT;
	}

	getViewSelector(): MemoizedSelector<object, ViewInfo | undefined, DefaultProjectorFn<ViewInfo | undefined>> {
		return GET_ARCHIVE_VIEW;
	}

	createChangeViewAction(viewInfo: ViewInfo): ChangeArchivesView {
		return new ChangeArchivesView(viewInfo);
	}

}
