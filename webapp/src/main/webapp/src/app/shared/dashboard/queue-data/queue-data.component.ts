import {Component, Input, OnDestroy, OnInit} from "@angular/core";
import {Subscription} from 'rxjs';
import {select, Store} from "@ngrx/store";
import {QueueData} from "./queue-data";
import {SyncMode} from "../../sync-mode.enum";
import {DashboardService} from "../../dashboard.service";
import {FetchCount} from "../../state/dashboard.actions";

@Component({
	selector: 'queue-data',
	templateUrl: './queue-data.component.html'
})
export class QueueDataComponent implements OnInit, OnDestroy {

	data = new QueueData();

	categorizationLabel?: string;

	@Input()
	syncMode?: SyncMode;

	@Input()
	entityType: string = '';

	timeoutId?: number;

	countReceivedSub?: Subscription;

	constructor(private service: DashboardService, private store: Store) {
	}

	ngOnInit(): void {
		if (this.syncMode == SyncMode.RECEIVER) {
			this.categorizationLabel = $localize`:@@common-entity-breakdown:Entity Breakdown`;
		} else {
			this.categorizationLabel = $localize`:@@common-db-table-breakdown:Database Table Breakdown`;
		}

		this.countReceivedSub = this.store.pipe(select('')).subscribe(count => {
			if(count) {
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
		let queueName: string = '';
		if(this.entityType == 'SyncMessage'){
			queueName = 'sync';
		}
		this.store.dispatch(new FetchCount(this.entityType, queueName));
	}

	ngOnDestroy(): void {
		this.stopSubscriptions();
	}

	stopSubscriptions(): void {
		clearTimeout(this.timeoutId);
		this.countReceivedSub?.unsubscribe();
	}

}
