import {Component} from "@angular/core";
import {DashboardComponent} from "../../shared/dashboard.component";
import {SyncMode} from "../../receiver/shared/sync-mode.enum";

@Component({
	selector: 'sender-dashboard',
	templateUrl: './sender-dashboard.component.html'
})
export class SenderDashboardComponent extends DashboardComponent {

	getSyncMode(): SyncMode {
		return SyncMode.SENDER;
	}

}
