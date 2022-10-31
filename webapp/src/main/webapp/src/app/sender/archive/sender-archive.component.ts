import {Component, OnInit} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {Subscription} from 'rxjs';
import {BaseListingComponent} from 'src/app/shared/base-listing.component';
import {SenderSyncArchive} from './sender-archive';
import {SenderArchiveService} from './sender-archive.service';
import {SenderSyncArchiveCountAndItems} from './sender-sync-archive-count-and-items';
import {SenderArchivedLoaded} from './state/sender-archive.actions';
import {GET_SYNC_ARCHIVE} from './state/sender-archive.reducer';

@Component({
	selector: 'app-sender-archive',
	templateUrl: './sender-archive.component.html',
})
export class SenderArchiveComponent extends BaseListingComponent implements OnInit {

	count?: number;

	loadedSubscription?: Subscription;

	senderArchiveItems?: SenderSyncArchive[];

	startDate?: string;

	endDate?: string;

	constructor(private service: SenderArchiveService,
				private store: Store) {
		super();
	}

	ngOnInit() {
		this.init();
		this.loadedSubscription = this.store.pipe(select(GET_SYNC_ARCHIVE)).subscribe(
			countAndItems => {
				this.count = countAndItems.count;
				this.senderArchiveItems = countAndItems.items
				this.reRender();
			}
		);
		this.loadSenderArchiveData();
	}

	loadSenderArchiveData() {
		this.service.getArchiveCountAndItems().subscribe(countAndItems => {
			this.store.dispatch(new SenderArchivedLoaded(countAndItems));
		});
	}

	filterByEventDate() {
		let initialItems = new SenderSyncArchiveCountAndItems();
		initialItems.count = 0;
		initialItems.items = [];

		//Clear table content
		this.store.dispatch(new SenderArchivedLoaded(initialItems));
		let start = this.startDate != undefined ? this.startDate : '';
		let end = this.endDate != undefined ? this.endDate : '';
		this.service.searchByEventDate(start, end).subscribe(countAndItems => {
			this.store.dispatch(new SenderArchivedLoaded(countAndItems));
		});
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
		super.ngOnDestroy();
	}

}
