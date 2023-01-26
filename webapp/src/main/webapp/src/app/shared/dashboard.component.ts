import {Component, OnDestroy, OnInit} from "@angular/core";
import {DashboardService} from "./dashboard.service";
import {Dashboard} from "./dashboard";
import {Subscription, timer} from 'rxjs';
import {select, Store} from "@ngrx/store";
import {GET_PROPS} from "../state/app.reducer";
import {SyncMode} from "../receiver/shared/sync-mode.enum";

@Component({template: ''})
export abstract class DashboardComponent implements OnInit, OnDestroy {

	dashboard?: Dashboard;

	reloadTimer?: Subscription;

	propsLoaded?: Subscription;

	constructor(private service: DashboardService, private store: Store) {
	}

	ngOnInit(): void {
		this.propsLoaded = this.store.pipe(select(GET_PROPS)).subscribe(props => {
			if (props.syncMode == this.getSyncMode()) {
				this.reloadTimer = timer(0, 30000).subscribe(() => {
					this.loadDashboard();
				});
			}
		});
	}

	loadDashboard(): void {
		this.service.getDashboard().subscribe(dashboard => {
			this.dashboard = dashboard;
		});
	}

	ngOnDestroy(): void {
		this.reloadTimer?.unsubscribe();
		this.propsLoaded?.unsubscribe();
	}

	abstract getSyncMode(): SyncMode;

}
