import {Component} from '@angular/core';
import {NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {Site} from "../../site";

@Component({
	selector: 'reconcile-report',
	templateUrl: './reconcile-report.component.html'
})
export class ReconcileReportComponent {

	report?: [];

	modalRef?: NgbModalRef;

	sites?: Site[];

	constructor() {
	}

}
