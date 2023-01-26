import {Component} from "@angular/core";
import {DashboardComponent} from "../../shared/dashboard.component";
import {SyncMode} from "../shared/sync-mode.enum";

@Component({
	selector: 'receiver-dashboard',
	templateUrl: './receiver-dashboard.component.html'
})
export class ReceiverDashboardComponent extends DashboardComponent {

	getSyncMode(): SyncMode {
		return SyncMode.RECEIVER;
	}

}
