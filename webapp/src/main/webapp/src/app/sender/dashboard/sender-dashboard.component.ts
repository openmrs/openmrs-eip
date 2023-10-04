import {Component} from "@angular/core";
import {SyncMode} from "../../shared/sync-mode.enum";
import {DashboardComponent} from "../../shared/dashboard/dashboard.component";

@Component({
	selector: 'sender-dashboard',
	templateUrl: './sender-dashboard.component.html'
})
export class SenderDashboardComponent extends DashboardComponent {

	getSyncMode(): SyncMode {
		return SyncMode.SENDER;
	}

}
