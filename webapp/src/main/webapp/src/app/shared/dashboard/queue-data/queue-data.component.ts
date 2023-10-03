import {Component, Input, OnDestroy, OnInit} from "@angular/core";
import {Subscription} from 'rxjs';
import {select, Store} from "@ngrx/store";
import {QueueData} from "./queue-data";
import {SyncMode} from "../../sync-mode.enum";
import {DashboardService} from "../../dashboard.service";
import {FetchCount} from "./state/queue-data.actions";
import {GET_COUNT} from "./state/queue-data.reducer";

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

		this.countReceivedSub = this.store.pipe(select(GET_COUNT)).subscribe(count => {
			this.data.count = count;
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
		this.store.dispatch(new FetchCount(this.entityType));
	}

	ngOnDestroy(): void {
		this.stopSubscriptions();
	}

	stopSubscriptions(): void {
		clearTimeout(this.timeoutId);
		this.countReceivedSub?.unsubscribe();
	}

}
