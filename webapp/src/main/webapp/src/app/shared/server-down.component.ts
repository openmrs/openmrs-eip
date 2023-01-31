import {Component, Input} from "@angular/core";

@Component({
	selector: 'server-down',
	templateUrl: './server-down.component.html'
})
export class ServerDownComponent {

	@Input()
	serverDown = false;

	reload(): void {
		window.location.reload();
	}

}
