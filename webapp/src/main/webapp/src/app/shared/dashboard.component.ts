import {Component, OnInit} from "@angular/core";
import {DashboardService} from "./dashboard.service";
import {Dashboard} from "./dashboard";

@Component({template: ''})
export abstract class DashboardComponent implements OnInit {

	dashboard?: Dashboard;
	interval?: number;

	constructor(private service: DashboardService) {
	}

	ngOnInit(): void {
		this.loadDashboard();
		this.interval = setInterval(() => {
			this.loadDashboard();
		}, 60000);
	}

	loadDashboard(): void {
		this.service.getDashboard().subscribe(dashboard => {
			this.dashboard = dashboard;
		});
	}

	ngOnDestroy(): void {
		if (this.interval) {
			clearInterval(this.interval);
		}
	}

}
