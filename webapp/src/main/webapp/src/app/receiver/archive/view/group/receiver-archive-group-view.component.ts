import {Component} from '@angular/core';
import {Observable} from "rxjs";
import {Action, DefaultProjectorFn, MemoizedSelector, Store} from "@ngrx/store";
import {ModelClassPipe} from "../../../../shared/pipes/model-class.pipe";
import {BaseReceiverGroupViewComponent} from "../../../shared/base-receiver-group-view.component";
import {TotalCountAndGroupedItems} from "../../../../shared/total-count-and-grouped-items";
import {ReceiverSyncArchiveService} from "../../receiver-sync-archive.service";
import {GET_ARCHIVE_GRP_PROP_COUNT_MAP} from "../../state/receiver-archive.reducer";
import {GroupedArchivesLoaded} from "../../state/receiver-archive.actions";

@Component({
	selector: 'receiver-archive-group-view',
	templateUrl: './receiver-archive-group-view.component.html'
})
export class ReceiverArchiveGroupViewComponent extends BaseReceiverGroupViewComponent {

	constructor(private service: ReceiverSyncArchiveService, store: Store, classPipe: ModelClassPipe) {
		super(store, classPipe)
	}

	getTotalCountAndGroupedItems(groupProperty: string): Observable<TotalCountAndGroupedItems> {
		return this.service.searchByDateReceivedAndGroup('', '', groupProperty);
	}

	getSelector(): MemoizedSelector<object, Map<string, number> | undefined, DefaultProjectorFn<Map<string, number> | undefined>> {
		return GET_ARCHIVE_GRP_PROP_COUNT_MAP;
	}

	createLoadAction(countAndGroupedItems: TotalCountAndGroupedItems): Action {
		return new GroupedArchivesLoaded(countAndGroupedItems);
	}

}
