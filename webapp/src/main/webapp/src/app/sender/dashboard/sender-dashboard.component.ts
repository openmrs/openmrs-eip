import {Component} from "@angular/core";
import {DashboardComponent} from "../../shared/dashboard/dashboard.component";
import {select} from "@ngrx/store";
import {GET_DASHBOARD} from "../../shared/dashboard/state/dashboard.reducer";
import {LoadDashboard} from "../../shared/dashboard/state/dashboard.actions";
import {Dashboard} from "../../shared/dashboard/dashboard";
import {Subscription} from "rxjs";
import {SyncMode} from "../../shared/sync-mode.enum";
import {CountByStatusReceived, FetchCountByStatus} from "./state/sender.dashboard.actions";
import {GET_SYNC_COUNT_BY_STATUS} from "./state/sender.dashboard.reducer";

@Component({
	selector: 'sender-dashboard',
	templateUrl: './sender-dashboard.component.html'
})
export class SenderDashboardComponent extends DashboardComponent {

	dashboard?: Dashboard;

	timeoutId?: number;

	dashboardLoaded?: Subscription;

	statusAndCountFetched?: Subscription;

	statusAndCountMap?: Map<string, number>;

	processingSyncCountChange: boolean = false;

	getSyncMode(): SyncMode {
		return SyncMode.SENDER;
	}

	onInit(): void {
		this.statusAndCountFetched = this.store.pipe(select(GET_SYNC_COUNT_BY_STATUS)).subscribe(statusAndCountMap => {
			this.statusAndCountMap = statusAndCountMap;
			this.processingSyncCountChange = false;
		});

		this.dashboardLoaded = this.store.pipe(select(GET_DASHBOARD)).subscribe(dashboard => {
			this.dashboard = dashboard;
			if (this.reload) {
				this.loadDashboard(30000);
			}
		});

		this.loadDashboard(0);
	}

	onSyncCountChange(syncCount: number): void {
		if (syncCount > 0) {
			this.processingSyncCountChange = true;
			this.store.dispatch(new FetchCountByStatus());
		} else {
			//Clear
			this.store.dispatch(new CountByStatusReceived(new Map<string, number>()));
		}
	}

	loadDashboard(delay: number): void {
		this.timeoutId = setTimeout(() => {
			if (!this.reload) {
				this.reload = true;
			}

			this.store.dispatch(new LoadDashboard());
		}, delay);
	}

	stopSubscriptions(): void {
		clearTimeout(this.timeoutId);
		this.statusAndCountFetched?.unsubscribe();
		this.dashboardLoaded?.unsubscribe();
		super.stopSubscriptions();
	}

}
