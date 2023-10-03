import {Component, Input, OnDestroy, OnInit} from "@angular/core";
import {Subscription} from 'rxjs';
import {Store} from "@ngrx/store";
import {DashboardService} from "../dashboard.service";
import {QueueData} from "./queue-data";

@Component({
	selector: 'queue-data',
	templateUrl: './queue-data.component.html'
})
export class QueueDataComponent implements OnInit, OnDestroy {

	data = new QueueData();

	@Input()
	categorizationMsgCode?: string;

	categorizationMsg: string;

	timeoutId?: number;

	propsLoaded?: Subscription;

	constructor(private service: DashboardService, private store: Store) {
		this.categorizationMsg = $localize `:@@${this.categorizationMsgCode}:cool`;
	}

	ngOnInit(): void {

	}

	ngOnDestroy(): void {
		this.stopSubscriptions();
	}

	stopSubscriptions(): void {
		clearTimeout(this.timeoutId);
		this.propsLoaded?.unsubscribe();
	}

}
