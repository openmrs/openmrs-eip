import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {select, Store} from "@ngrx/store";
import {GET_TOTAL_COUNT} from "./state/sync-message.reducer";

@Component({
	selector: 'receiver-sync-messages',
	templateUrl: './receiver-sync-message.component.html'
})
export class ReceiverSyncMessageComponent implements OnInit, OnDestroy {

	count?: number;

	viewLabel?: string;

	view?: string;

	totalCountSubscription?: Subscription;

	constructor(private store: Store) {
	}

	ngOnInit(): void {
		this.totalCountSubscription = this.store.pipe(select(GET_TOTAL_COUNT)).subscribe(
			count => {
				this.count = count;
			}
		);

		this.changeView('list');
	}

	changeView(view: string) {
		//TODO Clear count and items in the store state
		this.view = view;
		switch (this.view) {
			case 'list':
				this.viewLabel = $localize`:@@common-list:List`;
				break;
			case 'site':
				this.viewLabel = $localize`:@@common-health-facility:Health Facility`;
				break;
			case 'entity':
				this.viewLabel = $localize`:@@common-entity:Entity`;
				break;
		}
	}

	ngOnDestroy(): void {
		this.totalCountSubscription?.unsubscribe();
	}

}
