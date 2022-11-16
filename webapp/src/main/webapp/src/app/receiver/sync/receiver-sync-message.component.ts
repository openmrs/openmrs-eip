import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {select, Store} from "@ngrx/store";
import {GET_TOTAL_COUNT, GET_VIEW} from "./state/sync-message.reducer";
import {View} from "../shared/view.enum";
import {ChangeView} from "./state/sync-message.actions";
import {ViewInfo} from "../shared/view-info";

@Component({
	selector: 'receiver-sync-messages',
	templateUrl: './receiver-sync-message.component.html'
})
export class ReceiverSyncMessageComponent implements OnInit, OnDestroy {

	count?: number;

	view = View;

	viewInfo?: ViewInfo;

	totalCountSubscription?: Subscription;

	viewSubscription?: Subscription;

	constructor(private store: Store) {
	}

	ngOnInit(): void {
		this.totalCountSubscription = this.store.pipe(select(GET_TOTAL_COUNT)).subscribe(
			count => {
				this.count = count;
			}
		);

		this.viewSubscription = this.store.pipe(select(GET_VIEW)).subscribe(
			viewInfo => {
				this.viewInfo = viewInfo;
			}
		);

		this.changeToListView();
	}

	changeToListView() {
		this.changeView(View.LIST, $localize`:@@common-list:List`);
	}

	changeToSiteView() {
		this.changeView(View.SITE, $localize`:@@common-health-facility:Health Facility`);
	}

	changeToEntityView() {
		this.changeView(View.ENTITY, $localize`:@@common-entity:Entity`);
	}

	changeView(selectedView: View, viewLabel: string) {
		if (this.viewInfo?.view != selectedView) {
			this.store.dispatch(new ChangeView(new ViewInfo(selectedView, viewLabel)));
		}
	}

	ngOnDestroy(): void {
		this.totalCountSubscription?.unsubscribe();
		this.viewSubscription?.unsubscribe();
	}

}
