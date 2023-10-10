import {Component, Input, OnDestroy, OnInit} from "@angular/core";
import {Subscription} from 'rxjs';
import {select, Store} from "@ngrx/store";
import {QueueData} from "./queue-data";
import {
	FetchQueueCategories,
	FetchQueueCategoryCount,
	FetchQueueCount,
	QueueCategoriesReceived,
	QueueCountReceived
} from "../state/dashboard.actions";
import {DashboardService} from "../dashboard.service";
import {
	GET_SYNC_CATEGORIES,
	GET_SYNC_CATEGORY_COUNTS,
	GET_SYNC_COUNT,
	GET_SYNCED_COUNT
} from "../state/dashboard.reducer";
import {Selector} from "@ngrx/store/src/models";
import {SyncOperation} from "../../sync-operation.enum";

@Component({
	selector: 'queue-data',
	templateUrl: './queue-data.component.html'
})
export class QueueDataComponent implements OnInit, OnDestroy {

	readonly SYNC_OPS = Object.values(SyncOperation);

	readonly receiverQueueNames = ['sync', 'synced'];

	readonly queueAndCountSelectorMap = new Map<string, Selector<object, number | undefined | null>>([
		['sync', GET_SYNC_COUNT],
		['synced', GET_SYNCED_COUNT]
	]);

	readonly queueAndCategoriesSelectorMap = new Map<string, Selector<object, string[] | undefined>>([
		['sync', GET_SYNC_CATEGORIES]
	]);

	readonly queueAndCatCountSelectorMap = new Map<string, Selector<object, Map<string, Map<SyncOperation, number>> | undefined>>([
		['sync', GET_SYNC_CATEGORY_COUNTS]
	]);

	readonly data = new QueueData();

	categorizationLabel?: string;

	@Input()
	queueName: string = '';

	countSelector: Selector<object, number | null | undefined> | undefined;

	categorySelector: Selector<object, string[] | undefined> | undefined;

	categoryAndCountsSelector: Selector<object, Map<string, Map<SyncOperation, number>> | undefined> | undefined;

	timeoutId?: number;

	receivedCategoryCounts: number = 0;

	countSubscription?: Subscription;

	categoriesSubscription?: Subscription;

	categoryCountSubscription?: Subscription;

	constructor(private service: DashboardService, private store: Store) {
		this.countSelector = this.queueAndCountSelectorMap.get(this.queueName);
		this.categorySelector = this.queueAndCategoriesSelectorMap.get(this.queueName);
		this.categoryAndCountsSelector = this.queueAndCatCountSelectorMap.get(this.queueName);
	}

	ngOnInit(): void {
		if (this.receiverQueueNames.indexOf(this.queueName) > -1) {
			this.categorizationLabel = $localize`:@@common-entity-breakdown:Entity Breakdown`;
		} else {
			this.categorizationLabel = $localize`:@@common-db-table-breakdown:Database Table Breakdown`;
		}

		if (this.countSelector) {
			this.countSubscription = this.store.pipe(select(this.countSelector)).subscribe(count => {
				this.onCountChange(count);
			});
		}

		if (this.categorySelector) {
			this.categoriesSubscription = this.store.pipe(select(this.categorySelector)).subscribe(categories => {
				this.onCategoriesChange(categories);
			});
		}

		if (this.categoryAndCountsSelector) {
			this.categoryCountSubscription = this.store.pipe(select(this.categoryAndCountsSelector)).subscribe(catAndCounts => {
				this.onCategoryCountsChange(catAndCounts);
			});
		}

		//Display placeholders
		this.scheduleLoadData(0);
	}

	scheduleReload(): void {
		this.scheduleLoadData(30000);
	}

	scheduleLoadData(delay: number): void {
		this.timeoutId = setTimeout(() => {
			if (delay > 0) {
				console.log('Reload queue data');
				this.reset();
			} else {
				console.log('Load data queue initial');
			}

			this.getCount();
		}, delay);
	}

	reset(): void {
		console.log('Reset');
		this.receivedCategoryCounts = 0;
		this.store.dispatch(new QueueCountReceived(null, this.queueName));
	}

	updateCount(count: number): void {
		this.store.dispatch(new QueueCountReceived(count, this.queueName));
	}

	clearCategories(): void {
		this.store.dispatch(new QueueCategoriesReceived([], this.queueName));
	}

	getCount(): void {
		this.store.dispatch(new FetchQueueCount(this.queueName));
	}

	getCategories(): void {
		this.store.dispatch(new FetchQueueCategories(this.queueName));
	}

	getCategoryCounts(): void {
		this.data.categories?.forEach(c => {
			this.SYNC_OPS.forEach(o => {
				this.store.dispatch(new FetchQueueCategoryCount(this.queueName, c, o));
			});
		});
	}

	onCountChange(count?: number | null): void {
		//Ignore because this is the first event when initializing the component state.
		if (count !== undefined) {
			//Don't update the UI yet because this is a count reset event
			if (count !== null) {
				this.data.count = count;
				if (this.data.count > 0) {
					this.getCategories();
				} else {
					this.clearCategories();
					this.scheduleReload();
				}
			}
		}
	}

	onCategoriesChange(categories?: string[]): void {
		//Ignore because this is the first value when initializing the component state.
		if (categories) {
			console.log('Received categories: ' + categories?.length);
			this.data.categories = categories;
			if (this.data.categories.length > 0) {
				this.getCategoryCounts();
			} else {
				this.updateCount(0);
				this.scheduleReload();
			}
		}
	}

	onCategoryCountsChange(catAndCounts?: Map<string, Map<SyncOperation, number>>): void {
		if (catAndCounts) {
			console.log('Received category counts: ' + catAndCounts);
			this.data.categoryAndCounts = catAndCounts;
			this.receivedCategoryCounts++;
			//We're done fetching all the queue data
			if (this.data.categories && this.receivedCategoryCounts == (this.data.categories.length * this.SYNC_OPS.length)) {
				console.log('Done loading queue data');
				console.log('');
				//Update queue count to match the sum of all the counts by type and operation
				let effectiveCount: number = 0;
				this.data.categoryAndCounts?.forEach((opAndCount: Map<SyncOperation, number>, category: string) => {
					opAndCount.forEach((count: number, op: SyncOperation) => {
						effectiveCount += count;
					});
				});

				this.updateCount(effectiveCount);
				this.scheduleReload();
			}
		}
	}

	ngOnDestroy(): void {
		this.stopSubscriptions();
	}

	stopSubscriptions(): void {
		clearTimeout(this.timeoutId);
		this.countSubscription?.unsubscribe();
		this.categoriesSubscription?.unsubscribe();
		this.categoryCountSubscription?.unsubscribe();
	}

}
