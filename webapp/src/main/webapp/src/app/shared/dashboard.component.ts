import {Component, OnInit} from "@angular/core";
import {DashboardService} from "./dashboard.service";
import {Dashboard} from "./dashboard";
import {Subscription, timer} from 'rxjs';

@Component({template: ''})
export abstract class DashboardComponent implements OnInit {

	dashboard?: Dashboard;

	reloadTimer?: Subscription;


	constructor(private service: DashboardService) {
	}

	ngOnInit(): void {
		this.reloadTimer = timer(100, 30000).subscribe(() => {
			this.loadDashboard();
		});
	}

	loadDashboard(): void {
		this.service.getDashboard().subscribe(dashboard => {
			this.dashboard = dashboard;
		});
	}

	ngOnDestroy(): void {
		this.reloadTimer?.unsubscribe();
	}

}
