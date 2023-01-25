import {Component, OnInit} from '@angular/core';
import {AppService} from "./app.service";
import {SyncMode} from "./receiver/shared/sync-mode.enum";

@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

	constructor(private appService: AppService) {
	}

	syncMode?: SyncMode;

	ngOnInit(): void {
		//TODO use nrgx by dispatching an action
		this.appService.getAppProperties().subscribe(appProperties => this.syncMode = appProperties.syncMode)
	}

}
