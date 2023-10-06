import {Component, Input, OnDestroy, OnInit} from "@angular/core";
import {Subscription} from 'rxjs';
import {select, Store} from "@ngrx/store";
import {QueueData} from "./queue-data";
import {FetchQueueCategories, FetchQueueCategoryCount, FetchQueueCount} from "../state/dashboard.actions";
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

	readonly queueAndCountSelectorMap = new Map<string, Selector<object, number | undefined>>([
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

				this.getCategoryCounts();
			});
		}

		let catCountSelector: Selector<object, Map<string, Map<SyncOperation, number>> | undefined> | undefined = this.queueAndCatCountSelectorMap.get(this.queueName);
		if (catCountSelector) {
			this.categoriesSubscription = this.store.pipe(select(catCountSelector)).subscribe(catAndCounts => {
				if (catAndCounts) {
					this.data.categoryAndCounts = catAndCounts;
				}

				//Update total count and refresh
				//Schedule next reload after all counts are received
			});
		}

		//Display placeholders
		this.getCount();
		this.getCategories();
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

	ngOnDestroy(): void {
		this.stopSubscriptions();
	}

	stopSubscriptions(): void {
		clearTimeout(this.timeoutId);
		this.categoriesSubscription?.unsubscribe();
		this.categoriesSubscription?.unsubscribe();
	}

}
