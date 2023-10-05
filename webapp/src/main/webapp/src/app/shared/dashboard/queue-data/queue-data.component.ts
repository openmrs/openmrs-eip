import {Component, Input, OnDestroy, OnInit} from "@angular/core";
import {Subscription} from 'rxjs';
import {select, Store} from "@ngrx/store";
import {QueueData} from "./queue-data";
import {FetchCount} from "../state/dashboard.actions";
import {DashboardService} from "../dashboard.service";
import {GET_SYNC_COUNT, GET_SYNCED_COUNT} from "../state/dashboard.reducer";
import {Selector} from "@ngrx/store/src/models";

@Component({
	selector: 'queue-data',
	templateUrl: './queue-data.component.html'
})
export class QueueDataComponent implements OnInit, OnDestroy {

	receiverQueueNames: string[] = ['sync', 'synced'];

	queueAndCountSelectorMap = new Map<string, Selector<object, number | undefined>>([
		['sync', GET_SYNC_COUNT],
		['synced', GET_SYNCED_COUNT]
	]);

	queueAndTypeMap = new Map<string, string>([
		['sync', 'SyncMessage'],
		['synced', 'SyncedMessage']
	]);

	data = new QueueData();

	categorizationLabel?: string;

	@Input()
	queueName: string = '';

	timeoutId?: number;

	countReceivedSub?: Subscription;

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
			this.countReceivedSub = this.store.pipe(select(countSelector)).subscribe(count => {
				if (count) {
					this.data.count = count;
				}
			});
		}

		//Display placeholders
		//Get count and refresh
		this.getCount();
		//Get categories and refresh to display placeholders for each category count
		//Get the count for each category and refresh
		//Update count and refresh
		//Schedule next reload
	}

	getCount(): void {
		let entityType: string | undefined = this.queueAndTypeMap.get(this.queueName);
		if (entityType) {
			this.store.dispatch(new FetchCount(entityType, this.queueName));
		}
	}

	ngOnDestroy(): void {
		this.stopSubscriptions();
	}

	stopSubscriptions(): void {
		clearTimeout(this.timeoutId);
		this.countReceivedSub?.unsubscribe();
	}

}
