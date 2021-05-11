import {Component} from '@angular/core';

@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.scss']
})
export class AppComponent {

	title: string = 'dbsync';

	//TODO set based on sync mode using store state
	activeTab: string = "receiver";

}
