import {Component, OnDestroy, OnInit} from "@angular/core";
import {DashboardService} from "./dashboard.service";
import {Dashboard} from "./dashboard";
import {Subscription} from 'rxjs';
import {select, Store} from "@ngrx/store";
import {GET_PROPS} from "../state/app.reducer";
import {SyncMode} from "./sync-mode.enum";
import {LoadDashboard} from "./state/dashboard.actions";
import {GET_DASHBOARD, GET_DASHBOARD_ERROR} from "./state/dashboard.reducer";
import {HttpErrorResponse} from "@angular/common/http";

@Component({template: ''})
export abstract class DashboardComponent implements OnInit, OnDestroy {

	dashboard?: Dashboard;

	timeoutId?: number;

	propsLoaded?: Subscription;

	dashboardLoaded?: Subscription;

	dashboardError?: Subscription;

	serverDown = false;

	reload = false;

	constructor(private service: DashboardService, private store: Store) {
	}

	ngOnInit(): void {
		this.propsLoaded = this.store.pipe(select(GET_PROPS)).subscribe(props => {
			if (props.syncMode == this.getSyncMode()) {
				this.dashboardLoaded = this.store.pipe(select(GET_DASHBOARD)).subscribe(dashboard => {
					this.dashboard = dashboard;
					if (this.reload) {
						this.loadDashboard(30000);
					}
				});

				this.dashboardError = this.store.pipe(select(GET_DASHBOARD_ERROR)).subscribe(error => {
					this.handleLoadError(error);
				});

				this.loadDashboard(0);
			}
		});
	}

	loadDashboard(delay: number): void {
		this.timeoutId = setTimeout(() => {
			if (!this.reload) {
				this.reload = true;
			}

			this.store.dispatch(new LoadDashboard());
		}, delay);
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
		clearTimeout(this.timeoutId);
		this.propsLoaded?.unsubscribe();
		this.dashboardLoaded?.unsubscribe();
		this.dashboardError?.unsubscribe();
	}

	abstract getSyncMode(): SyncMode;

}
