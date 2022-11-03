import {Component, OnInit} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {Subscription} from 'rxjs';
import {BaseListingComponent} from 'src/app/shared/base-listing.component';
import {ReceiverSyncArchive} from './receiver-sync-archive';
import {ReceiverSyncArchiveCountAndItems} from './receiver-sync-archive-count-and-items';
import {ReceiverSyncArchiveService} from './receiver-sync-archive.service';
import {ReceiverArchiveLoaded} from './state/receiver-archive.actions';
import {GET_SYNC_ARCHIVE} from './state/receiver-archive.reducer';

@Component({
	selector: 'app-archive',
	templateUrl: './receiver-archive.component.html',
})
export class ReceiverArchiveComponent extends BaseListingComponent implements OnInit {

	count?: number;

	archives?: ReceiverSyncArchive[];

	loadedSubscription?: Subscription;

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
			countAndItems => {
				this.count = countAndItems?.count;
				this.archives = countAndItems?.items;
				this.reRender();
			}
		);

		this.loadArchives();

	}

	loadArchives(): void {
		this.service.getSyncArchiveCountAndItems().subscribe(countAndItems => {
			this.store.dispatch(new ReceiverArchiveLoaded(countAndItems));
		});
	}

	filterByDateReceived() {
		let initialItems = new ReceiverSyncArchiveCountAndItems();
		initialItems.count = 0;
		initialItems.items = [];

		//Clear table content
		this.store.dispatch(new ReceiverArchiveLoaded(initialItems));
		let start = this.startDate != undefined ? this.startDate : '';
		let end = this.endDate != undefined ? this.endDate : '';
		this.service.searchByDateReceived(start, end).subscribe(countAndItems => {
			this.store.dispatch(new ReceiverArchiveLoaded(countAndItems));
		});
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
		super.ngOnDestroy();
	}
}
