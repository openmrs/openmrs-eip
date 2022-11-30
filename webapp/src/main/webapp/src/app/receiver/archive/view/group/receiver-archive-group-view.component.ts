import {Component, Input} from '@angular/core';
import {Observable} from "rxjs";
import {Action, DefaultProjectorFn, MemoizedSelector, Store} from "@ngrx/store";
import {ModelClassPipe} from "../../../../shared/pipes/model-class.pipe";
import {BaseReceiverGroupViewComponent} from "../../../shared/base-receiver-group-view.component";
import {TotalCountAndGroupedItems} from "../../../../shared/total-count-and-grouped-items";
import {ReceiverSyncArchiveService} from "../../receiver-sync-archive.service";
import {GET_ARCHIVE_GRP_PROP_COUNT_MAP} from "../../state/receiver-archive.reducer";
import {GroupedArchivesLoaded} from "../../state/receiver-archive.actions";
import {DateRange} from "../../../../shared/date-range";
import {View} from "../../../shared/view.enum";

@Component({
	selector: 'receiver-archive-group-view',
	templateUrl: './receiver-archive-group-view.component.html'
})
export class ReceiverArchiveGroupViewComponent extends BaseReceiverGroupViewComponent {

	@Input()
	filterDateRange?: DateRange;

	constructor(private service: ReceiverSyncArchiveService, store: Store, classPipe: ModelClassPipe) {
		super(store, classPipe)
	}

	getTotalCountAndGroupedItems(groupProperty: string): Observable<TotalCountAndGroupedItems> {
		if (this.filterDateRange) {
			return this.doFilterByDateReceived(this.filterDateRange);
		} else {
			return this.service.searchByDateReceivedAndGroup('', '', groupProperty);
		}
	}

	getSelector(): MemoizedSelector<object, Map<string, number> | undefined, DefaultProjectorFn<Map<string, number> | undefined>> {
		return GET_ARCHIVE_GRP_PROP_COUNT_MAP;
	}

	createLoadAction(countAndGroupedItems: TotalCountAndGroupedItems): Action {
		return new GroupedArchivesLoaded(countAndGroupedItems);
	}

	getClearAction(): Action {
		let displayArchives = new TotalCountAndGroupedItems();
		displayArchives.count = 0;
		displayArchives.items = new Map();
		return new GroupedArchivesLoaded(displayArchives);
	}

	filterByDateReceived(dateRange?: DateRange) {
		this.doFilterByDateReceived(dateRange).subscribe(countAndItems => {
			this.store.dispatch(this.createLoadAction(countAndItems));
		});
	}

	doFilterByDateReceived(dateRange?: DateRange): Observable<TotalCountAndGroupedItems> {
		let start: string = dateRange?.start != undefined ? dateRange.start : '';
		let end: string = dateRange?.end != undefined ? dateRange.end : '';
		let groupBy: string = 'site';
		if (this.viewInfo?.view == View.ENTITY) {
			groupBy = 'modelClassName';
		}

		return this.service.searchByDateReceivedAndGroup(start, end, groupBy);
	}

}
