import {Component, ViewChild} from '@angular/core';
import {DefaultProjectorFn, MemoizedSelector, select, Store} from '@ngrx/store';
import {ViewInfo} from "../shared/view-info";
import {
	GET_ARCHIVE_FILTER_DATE_RANGE,
	GET_ARCHIVE_TOTAL_COUNT,
	GET_ARCHIVE_VIEW
} from "./state/receiver-archive.reducer";
import {BaseReceiverMultipleViewComponent} from "../shared/base-receiver-multiple-view.component";
import {ChangeArchivesView, FilterArchives} from "./state/receiver-archive.actions";
import {DateRange} from "../../shared/date-range";
import {Subscription} from "rxjs";
import {ReceiverArchiveListViewComponent} from "./view/list/receiver-archive-list-view.component";
import {ReceiverArchiveGroupViewComponent} from "./view/group/receiver-archive-group-view.component";
import {View} from "../shared/view.enum";

@Component({
	selector: 'receiver-archives',
	templateUrl: './receiver-archive.component.html',
})
export class ReceiverArchiveComponent extends BaseReceiverMultipleViewComponent {

	startDate?: string;

	endDate?: string;

	filterSubscription?: Subscription;

	filterDateRange?: DateRange;

	@ViewChild(ReceiverArchiveListViewComponent)
	listView?: ReceiverArchiveListViewComponent;

	@ViewChild(ReceiverArchiveGroupViewComponent)
	groupView?: ReceiverArchiveGroupViewComponent;

	constructor(store: Store) {
		super(store);
	}

	ngOnInit() {
		this.filterSubscription = this.store.pipe(select(GET_ARCHIVE_FILTER_DATE_RANGE)).subscribe(
			dateRange => {
				this.filterByDateReceived(dateRange);
			}
		);

		super.ngOnInit();
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

	reset() {
		//Clear the filters from the state so that when the component is redrawn there is none
		this.store.dispatch(new FilterArchives(undefined));
	}

	applyDateReceivedFilter() {
		let clearAction: any;
		if (this.viewInfo?.view == View.LIST) {
			clearAction = this.listView?.getClearAction();
		} else {
			clearAction = this.groupView?.getClearAction();
		}

		//Clear table content before the filter is applied
		this.store.dispatch(clearAction);
		this.store.dispatch(new FilterArchives(new DateRange(this.startDate, this.endDate)));
	}

	filterByDateReceived(dateRange?: DateRange) {
		this.filterDateRange = dateRange;
		if (this.viewInfo?.view == View.LIST) {
			this.listView?.filterByDateReceived(this.filterDateRange);
		} else {
			this.groupView?.filterByDateReceived(this.filterDateRange);
		}
	}

	ngOnDestroy(): void {
		super.ngOnDestroy();
		this.filterSubscription?.unsubscribe();
		this.reset();
	}

}
