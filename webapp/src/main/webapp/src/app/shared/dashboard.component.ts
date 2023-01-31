import {Component, OnDestroy, OnInit} from "@angular/core";
import {DashboardService} from "./dashboard.service";
import {Dashboard} from "./dashboard";
import {Subscription, timer} from 'rxjs';
import {select, Store} from "@ngrx/store";
import {GET_PROPS} from "../state/app.reducer";
import {SyncMode} from "../receiver/shared/sync-mode.enum";
import {LoadDashboard} from "./state/dashboard.actions";
import {GET_DASHBOARD, GET_DASHBOARD_ERROR} from "./state/dashboard.reducer";
import {HttpErrorResponse} from "@angular/common/http";

@Component({template: ''})
export abstract class DashboardComponent implements OnInit, OnDestroy {

	dashboard?: Dashboard;

	reloadTimer?: Subscription;

	propsLoaded?: Subscription;

	dashboardLoaded?: Subscription;

	dashboardError?: Subscription;

	serverDown = false;

	constructor(private service: DashboardService, private store: Store) {
	}

	ngOnInit(): void {
		this.propsLoaded = this.store.pipe(select(GET_PROPS)).subscribe(props => {
			if (props.syncMode == this.getSyncMode()) {
				this.dashboardLoaded = this.store.pipe(select(GET_DASHBOARD)).subscribe(dashboard => {
					this.dashboard = dashboard;
				});

				this.dashboardError = this.store.pipe(select(GET_DASHBOARD_ERROR)).subscribe(error => {
					this.handleLoadError(error);
				});

				this.reloadTimer = timer(0, 30000).subscribe(() => {
					this.store.dispatch(new LoadDashboard());
				});
			}
		});
	}

	handleLoadError(error: HttpErrorResponse): void {
		if (error) {
			if (error.status === 0) {
				this.serverDown = true;
				this.stopSubscriptions();
			} else {
				throw error;
			}
		}
	}

	ngOnDestroy(): void {
		this.stopSubscriptions();
	}

	stopSubscriptions(): void {
		this.reloadTimer?.unsubscribe();
		this.propsLoaded?.unsubscribe();
		this.dashboardLoaded?.unsubscribe();
		this.dashboardError?.unsubscribe();
	}

	abstract getSyncMode(): SyncMode;

}
