import {Component, OnDestroy, OnInit} from "@angular/core";
import {DashboardService} from "./dashboard.service";
import {Dashboard} from "./dashboard";
import {Subscription, timer} from 'rxjs';
import {select, Store} from "@ngrx/store";
import {GET_PROPS} from "../state/app.reducer";
import {SyncMode} from "../receiver/shared/sync-mode.enum";
import {LoadDashboard} from "./state/dashboard.actions";
import {GET_DASHBOARD} from "./state/dashboard.reducer";

@Component({template: ''})
export abstract class DashboardComponent implements OnInit, OnDestroy {

	dashboard?: Dashboard;

	reloadTimer?: Subscription;

	propsLoaded?: Subscription;

	dashboardLoaded?: Subscription;

	constructor(private service: DashboardService, private store: Store) {
	}

	ngOnInit(): void {
		this.propsLoaded = this.store.pipe(select(GET_PROPS)).subscribe(props => {
			if (props.syncMode == this.getSyncMode()) {
				this.dashboardLoaded = this.store.pipe(select(GET_DASHBOARD)).subscribe(dashboard => {
					this.dashboard = dashboard;
				});

				this.reloadTimer = timer(0, 30000).subscribe(() => {
					this.store.dispatch(new LoadDashboard());
				});
			}
		});
	}

	ngOnDestroy(): void {
		this.reloadTimer?.unsubscribe();
		this.propsLoaded?.unsubscribe();
		this.dashboardLoaded?.unsubscribe();
	}

	abstract getSyncMode(): SyncMode;

}
