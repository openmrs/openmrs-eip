import {Component, OnDestroy, OnInit} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {Subscription} from 'rxjs';
import {View} from "../shared/view.enum";
import {ViewInfo} from "../shared/view-info";
import {GET_TOTAL_COUNT, GET_VIEW} from "./state/receiver-archive.reducer";
import {ChangeView} from "./state/receiver-archive.actions";

@Component({
	selector: 'receiver-archives',
	templateUrl: './receiver-archive.component.html',
})
export class ReceiverArchiveComponent implements OnInit, OnDestroy {

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
		this.store.dispatch(new ChangeView(new ViewInfo(selectedView, viewLabel)));
	}

	ngOnDestroy(): void {
		this.totalCountSubscription?.unsubscribe();
		this.viewSubscription?.unsubscribe();
	}
}
