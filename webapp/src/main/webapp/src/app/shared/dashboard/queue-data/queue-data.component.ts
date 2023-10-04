import {Component, Input, OnDestroy, OnInit} from "@angular/core";
import {Subscription} from 'rxjs';
import {select, Store} from "@ngrx/store";
import {QueueData} from "./queue-data";
import {FetchCount} from "../state/dashboard.actions";
import {DashboardService} from "../dashboard.service";
import {GET_SYNC_COUNT} from "../state/dashboard.reducer";

@Component({
	selector: 'queue-data',
	templateUrl: './queue-data.component.html'
})
export class QueueDataComponent implements OnInit, OnDestroy {

	receiverQueueNames: string[] = ['sync'];

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

		this.countReceivedSub = this.store.pipe(select(GET_SYNC_COUNT)).subscribe(count => {
			if (count) {
				this.data.count = count;
			}
		});

		//Display placeholders
		//Get count and refresh
		this.getCount();
		//Get categories and refresh to display placeholders for each category count
		//Get the count for each category and refresh
		//Update count and refresh
		//Schedule next reload
	}

	getCount(): void {
		let entityType: string = '';
		if (this.queueName == 'sync') {
			entityType = 'SyncMessage';
		}

		this.store.dispatch(new FetchCount(entityType, this.queueName));
	}

	ngOnDestroy(): void {
		this.stopSubscriptions();
	}

	stopSubscriptions(): void {
		clearTimeout(this.timeoutId);
		this.countReceivedSub?.unsubscribe();
	}

}
