import {Component} from "@angular/core";
import {SyncMode} from "../../shared/sync-mode.enum";
import {DashboardComponent} from "../../shared/dashboard/dashboard.component";

@Component({
	selector: 'receiver-dashboard',
	templateUrl: './receiver-dashboard.component.html'
})
export class ReceiverDashboardComponent extends DashboardComponent {

	getSyncMode(): SyncMode {
		return SyncMode.RECEIVER;
	}

}
