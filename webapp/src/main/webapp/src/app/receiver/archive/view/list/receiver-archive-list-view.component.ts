import {Component, Input, OnInit} from '@angular/core';
import {Action, select, Store} from '@ngrx/store';
import {Subscription} from 'rxjs';
import {BaseListingComponent} from 'src/app/shared/base-listing.component';
import {ReceiverSyncArchive} from "../../receiver-sync-archive";
import {ReceiverSyncArchiveService} from "../../receiver-sync-archive.service";
import {GET_SYNC_ARCHIVE} from "../../state/receiver-archive.reducer";
import {ReceiverArchiveLoaded} from "../../state/receiver-archive.actions";
import {DateRange} from "../../../../shared/date-range";
import {ReceiverSyncArchiveCountAndItems} from "../../receiver-sync-archive-count-and-items";

@Component({
	selector: 'receiver-archive-list-view',
	templateUrl: './receiver-archive-list-view.component.html',
})
export class ReceiverArchiveListViewComponent extends BaseListingComponent implements OnInit {

	archives?: ReceiverSyncArchive[];

	loadedSubscription?: Subscription;

	@Input()
	filterDateRange?: DateRange;

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

		this.loadArchives();

	}

	loadArchives(): void {
		if (this.filterDateRange) {
			this.filterByDateReceived(this.filterDateRange);
		} else {
			this.service.getSyncArchiveCountAndItems().subscribe(countAndItems => {
				this.store.dispatch(new ReceiverArchiveLoaded(countAndItems));
			});
		}
	}

	getClearAction(): Action {
		let displayArchives = new ReceiverSyncArchiveCountAndItems();
		displayArchives.count = 0;
		displayArchives.items = [];
		return new ReceiverArchiveLoaded(displayArchives);
	}

	filterByDateReceived(dateRange?: DateRange) {
		let start: string = dateRange?.start != undefined ? dateRange.start : '';
		let end: string = dateRange?.end != undefined ? dateRange.end : '';
		this.service.searchByDateReceived(start, end).subscribe(countAndItems => {
			this.store.dispatch(new ReceiverArchiveLoaded(countAndItems));
		});
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
		super.ngOnDestroy();
	}
}
