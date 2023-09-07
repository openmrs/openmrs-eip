import {Component} from "@angular/core";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
	templateUrl: './message.component.html'
})
export class MessageDialogComponent {

	message?: string;

	theme?: string;

	constructor(public activeModal: NgbActiveModal) {
	}

}
