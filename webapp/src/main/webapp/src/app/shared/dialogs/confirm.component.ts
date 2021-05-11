import {Component} from "@angular/core";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
	templateUrl: './confirm.component.html'
})
export class ConfirmDialogComponent {

	title?: String;

	message?: String;

	constructor(public activeModal: NgbActiveModal) {
	}

}
