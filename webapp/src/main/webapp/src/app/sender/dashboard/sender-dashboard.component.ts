import {Component} from "@angular/core";
import {DashboardComponent} from "../../shared/dashboard/dashboard.component";
import {select} from "@ngrx/store";
import {GET_DASHBOARD} from "../../shared/dashboard/state/dashboard.reducer";
import {LoadDashboard} from "../../shared/dashboard/state/dashboard.actions";
import {Dashboard} from "../../shared/dashboard/dashboard";
import {Subscription} from "rxjs";
import {SyncMode} from "../../shared/sync-mode.enum";

@Component({
	selector: 'sender-dashboard',
	templateUrl: './sender-dashboard.component.html'
})
export class SenderDashboardComponent extends DashboardComponent {

	dashboard?: Dashboard;

	timeoutId?: number;

	dashboardLoaded?: Subscription;

	getSyncMode(): SyncMode {
		return SyncMode.SENDER;
	}

	onInit(): void {
		this.dashboardLoaded = this.store.pipe(select(GET_DASHBOARD)).subscribe(dashboard => {
			this.dashboard = dashboard;
			if (this.reload) {
				this.loadDashboard(30000);
			}
		});

		this.loadDashboard(0);
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
		this.dashboardLoaded?.unsubscribe();
		super.stopSubscriptions();
	}

}
