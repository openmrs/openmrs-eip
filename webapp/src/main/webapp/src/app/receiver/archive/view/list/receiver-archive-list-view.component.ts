import {Component, OnInit} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {Subscription} from 'rxjs';
import {BaseListingComponent} from 'src/app/shared/base-listing.component';
import {ReceiverSyncArchive} from "../../receiver-sync-archive";
import {ReceiverSyncArchiveService} from "../../receiver-sync-archive.service";
import {GET_ARCHIVE_FILTER_DATE_RANGE, GET_SYNC_ARCHIVE} from "../../state/receiver-archive.reducer";
import {FilterArchives, ReceiverArchiveLoaded} from "../../state/receiver-archive.actions";
import {ReceiverSyncArchiveCountAndItems} from "../../receiver-sync-archive-count-and-items";
import {DateRange} from "../../../../shared/date-range";

@Component({
	selector: 'receiver-archive-list-view',
	templateUrl: './receiver-archive-list-view.component.html',
})
export class ReceiverArchiveListViewComponent extends BaseListingComponent implements OnInit {

	count?: number;

	archives?: ReceiverSyncArchive[];

	loadedSubscription?: Subscription;

	filterSubscription?: Subscription;

	startDate?: string;

	endDate?: string;

	constructor(
		private service: ReceiverSyncArchiveService,
		private store: Store,
	) {
		super();
	}

	ngOnInit(): void {
		this.init();
		this.loadedSubscription = this.store.pipe(select(GET_SYNC_ARCHIVE)).subscribe(
			archives => {
				this.archives = archives;
				this.reRender();
			}
		);

		this.filterSubscription = this.store.pipe(select(GET_ARCHIVE_FILTER_DATE_RANGE)).subscribe(
			dateRange => this.doFilterByDateReceived(dateRange)
		);

		this.loadArchives();

	}

	loadArchives(): void {
		this.service.getSyncArchiveCountAndItems().subscribe(countAndItems => {
			this.store.dispatch(new ReceiverArchiveLoaded(countAndItems));
		});
	}

	filterByDateReceived() {
		let noItems = new ReceiverSyncArchiveCountAndItems();
		noItems.count = 0;
		noItems.items = [];

		//Clear table content
		this.store.dispatch(new ReceiverArchiveLoaded(noItems));
		this.store.dispatch(new FilterArchives(new DateRange(this.startDate, this.endDate)));
	}

	doFilterByDateReceived(dateRange?: DateRange) {
		let start: string = dateRange?.start != undefined ? dateRange.start : '';
		let end: string = dateRange?.end != undefined ? dateRange.end : '';
		this.service.searchByDateReceived(start, end).subscribe(countAndItems => {
			this.store.dispatch(new ReceiverArchiveLoaded(countAndItems));
		});
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
		this.filterSubscription?.unsubscribe()
		super.ngOnDestroy();
	}
}
