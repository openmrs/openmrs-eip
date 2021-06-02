import {Component, OnInit} from "@angular/core";
import {DashboardService} from "./dashboard.service";
import {Dashboard} from "./dashboard";

@Component({template: ''})
export abstract class DashboardComponent implements OnInit {

	dashboard?: Dashboard;

	unCategorizedLabel: string = $localize`:@@common-error-uncategorized:Uncategorized`;

	constructor(private service: DashboardService) {
	}

	ngOnInit(): void {
		this.service.getDashboard().subscribe(dashboard => {
			this.dashboard = dashboard;
		})
	}

}
