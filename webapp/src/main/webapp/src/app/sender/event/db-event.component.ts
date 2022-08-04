import {Component, OnInit} from "@angular/core";
import {BaseListingComponent} from "../../shared/base-listing.component";
import {Subscription} from "rxjs";
import {select, Store} from "@ngrx/store";
import {DbEvent} from "./db-event";
import {DbEventsLoaded} from "./state/db-event.actions";
import {GET_EVENTS} from "./state/db-event.reducer";
import {DbEventService} from "./db-event.service";

@Component({
	selector: 'db-events',
	templateUrl: './db-event.component.html'
})
export class DbEventComponent extends BaseListingComponent implements OnInit {

	count?: number;

	events?: DbEvent[];

	loadedSubscription?: Subscription;

	constructor(
		private service: DbEventService,
		private store: Store
	) {
		super();
	}

	ngOnInit(): void {
		this.init();
		this.loadedSubscription = this.store.pipe(select(GET_EVENTS)).subscribe(
			countAndItems => {
				this.count = countAndItems.count;
				this.events = countAndItems.items;
				this.reRender();
			}
		);

		this.loadEvents();
	}

	loadEvents(): void {
		this.service.getEventCountAndItems().subscribe(countAndItems => {
			this.store.dispatch(new DbEventsLoaded(countAndItems));
		});
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
		super.ngOnDestroy();
	}

}
