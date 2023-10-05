import {Component, Input, OnDestroy, OnInit} from "@angular/core";
import {Subscription} from 'rxjs';
import {select, Store} from "@ngrx/store";
import {QueueData} from "./queue-data";
import {FetchQueueCategories, FetchQueueCount} from "../state/dashboard.actions";
import {DashboardService} from "../dashboard.service";
import {GET_SYNC_CATEGORIES, GET_SYNC_COUNT, GET_SYNCED_COUNT} from "../state/dashboard.reducer";
import {Selector} from "@ngrx/store/src/models";

@Component({
	selector: 'queue-data',
	templateUrl: './queue-data.component.html'
})
export class QueueDataComponent implements OnInit, OnDestroy {

	receiverQueueNames = ['sync', 'synced'];

	queueAndTypeMap = new Map<string, string>([
		['sync', 'SyncMessage'],
		['synced', 'SyncedMessage']
	]);

	queueAndCountSelectorMap = new Map<string, Selector<object, number | undefined>>([
		['sync', GET_SYNC_COUNT],
		['synced', GET_SYNCED_COUNT]
	]);

	queueAndCategoriesSelectorMap = new Map<string, Selector<object, string[] | undefined>>([
		['sync', GET_SYNC_CATEGORIES]
	]);

	data = new QueueData();

	categorizationLabel?: string;

	@Input()
	queueName: string = '';

	timeoutId?: number;

	countSubscription?: Subscription;

	categoriesSubscription?: Subscription;

	constructor(private service: DashboardService, private store: Store) {
	}

	ngOnInit(): void {
		if (this.receiverQueueNames.indexOf(this.queueName) > -1) {
			this.categorizationLabel = $localize`:@@common-entity-breakdown:Entity Breakdown`;
		} else {
			this.categorizationLabel = $localize`:@@common-db-table-breakdown:Database Table Breakdown`;
		}

		let countSelector: Selector<object, number | undefined> | undefined = this.queueAndCountSelectorMap.get(this.queueName);
		if (countSelector) {
			this.countSubscription = this.store.pipe(select(countSelector)).subscribe(count => {
				if (count) {
					this.data.count = count;
				}
			});
		}

		let catSelector: Selector<object, string[] | undefined> | undefined = this.queueAndCategoriesSelectorMap.get(this.queueName);
		if (catSelector) {
			this.categoriesSubscription = this.store.pipe(select(catSelector)).subscribe(categories => {
				if (categories) {
					this.data.categories = categories;
				}
			});
		}

		//Display placeholders
		//Get count and refresh
		this.getCount();
		this.getCategories();
		//Get categories and refresh to display placeholders for each category count
		//Get the count for each category and refresh
		//Update count and refresh
		//Schedule next reload
	}

	getCount(): void {
		let entityType: string | undefined = this.queueAndTypeMap.get(this.queueName);
		if (entityType) {
			this.store.dispatch(new FetchQueueCount(entityType, this.queueName));
		}
	}

	getCategories(): void {
		let entityType: string | undefined = this.queueAndTypeMap.get(this.queueName);
		if (entityType) {
			this.store.dispatch(new FetchQueueCategories(entityType, this.queueName));
		}
	}

	ngOnDestroy(): void {
		this.stopSubscriptions();
	}

	stopSubscriptions(): void {
		clearTimeout(this.timeoutId);
		this.categoriesSubscription?.unsubscribe();
		this.categoriesSubscription?.unsubscribe();
	}

}
