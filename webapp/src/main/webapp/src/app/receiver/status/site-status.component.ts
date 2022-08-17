import {Component, OnInit} from '@angular/core';
import {BaseListingComponent} from "../../shared/base-listing.component";
import {SiteStatus} from "./site-status";
import {Subscription} from "rxjs";
import {select, Store} from "@ngrx/store";
import {GET_STATUSES} from "./state/site-status.reducer";
import {SiteStatusService} from "./site-status.service";
import {SiteStatusesLoaded} from "./state/site-status.actions";

@Component({
	selector: 'site-statuses',
	templateUrl: './site-status.component.html'
})
export class SiteStatusComponent extends BaseListingComponent implements OnInit {

	count?: number;

	statuses?: SiteStatus[];

	loadedSubscription?: Subscription;

	constructor(
		private service: SiteStatusService,
		private store: Store
	) {
		super();
	}

	ngOnInit(): void {
		this.init();
		this.loadedSubscription = this.store.pipe(select(GET_STATUSES)).subscribe(
			countAndItems => {
				this.count = countAndItems.count;
				this.statuses = countAndItems.items;
				this.reRender();
			}
		);

		this.loadStatuses();
	}

	loadStatuses(): void {
		this.service.getStatusCountAndItems().subscribe(countAndItems => {
			this.store.dispatch(new SiteStatusesLoaded(countAndItems));
		});
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
		super.ngOnDestroy();
	}

}
