import {Component} from "@angular/core";
import {DashboardComponent} from "../../shared/dashboard/dashboard.component";
import {select} from "@ngrx/store";
import {GET_DASHBOARD} from "../../shared/dashboard/state/dashboard.reducer";
import {LoadDashboard} from "../../shared/dashboard/state/dashboard.actions";
import {Dashboard} from "../../shared/dashboard/dashboard";
import {SyncMode} from "../../shared/sync-mode.enum";
import {FetchCountByStatus} from "./state/sender.dashboard.actions";
import {GET_SYNC_COUNT_BY_STATUS} from "./state/sender.dashboard.reducer";
import {Subscription} from "rxjs";

@Component({
	selector: 'sender-dashboard',
	templateUrl: './sender-dashboard.component.html'
})
export class SenderDashboardComponent extends DashboardComponent {

	dashboard?: Dashboard;

	dashboardTimeoutId?: number;

	countByStatusTimeoutId?: number;

	dashboardLoaded?: Subscription;

	statusAndCountFetched?: Subscription;

	statusAndCountMap?: Map<string, number>;

	getSyncMode(): SyncMode {
		return SyncMode.SENDER;
	}

	onInit(): void {
		this.statusAndCountFetched = this.store.pipe(select(GET_SYNC_COUNT_BY_STATUS)).subscribe(statusAndCountMap => {
			if (statusAndCountMap) {
				this.statusAndCountMap = statusAndCountMap;
				this.fetchSyncCountByStatus(30000);
			}
		});

		this.dashboardLoaded = this.store.pipe(select(GET_DASHBOARD)).subscribe(dashboard => {
			this.dashboard = dashboard;
			if (this.reload) {
				this.loadDashboard(30000);
			}
		});

		this.fetchSyncCountByStatus(100);
		this.loadDashboard(100);
	}

	fetchSyncCountByStatus(delay: number): void {
		this.countByStatusTimeoutId = setTimeout(() => {
			this.store.dispatch(new FetchCountByStatus());
		}, delay);
	}

	loadDashboard(delay: number): void {
		this.dashboardTimeoutId = setTimeout(() => {
			if (!this.reload) {
				this.reload = true;
			}

			this.store.dispatch(new LoadDashboard());
		}, delay);
	}

	stopSubscriptions(): void {
		clearTimeout(this.countByStatusTimeoutId);
		clearTimeout(this.dashboardTimeoutId);
		this.statusAndCountFetched?.unsubscribe();
		this.dashboardLoaded?.unsubscribe();
		super.stopSubscriptions();
	}

}
