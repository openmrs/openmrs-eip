import {Component, OnDestroy, OnInit} from "@angular/core";
import {Action, DefaultProjectorFn, MemoizedSelector, select, Store} from "@ngrx/store";
import {View} from "./view.enum";
import {ViewInfo} from "./view-info";
import {Subscription} from "rxjs";

@Component({template: ''})
export abstract class BaseReceiverMultipleViewComponent implements OnInit, OnDestroy {

	count?: number;

	view = View;

	viewInfo?: ViewInfo;

	totalCountSubscription?: Subscription;

	viewSubscription?: Subscription;

	protected constructor(protected store: Store) {
	}

	ngOnInit(): void {
		this.totalCountSubscription = this.store.pipe(select(this.getViewTotalCountSelector())).subscribe(
			count => {
				this.count = count;
			}
		);

		this.viewSubscription = this.store.pipe(select(this.getViewSelector())).subscribe(
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
			this.store.dispatch(this.createChangeViewAction(new ViewInfo(selectedView, viewLabel)));
		}
	}

	ngOnDestroy(): void {
		this.totalCountSubscription?.unsubscribe();
		this.viewSubscription?.unsubscribe();
	}

	abstract getViewTotalCountSelector(): MemoizedSelector<object, number | undefined, DefaultProjectorFn<number | undefined>>;

	abstract getViewSelector(): MemoizedSelector<object, ViewInfo | undefined, DefaultProjectorFn<ViewInfo | undefined>>;

	abstract createChangeViewAction(viewInfo: ViewInfo): Action;

}
