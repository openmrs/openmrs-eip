import {Component} from '@angular/core';
import {NgbModalRef} from "@ng-bootstrap/ng-bootstrap";

@Component({
	selector: 'reconcile-report',
	templateUrl: './reconcile-report.component.html'
})
export class ReconcileReportComponent {

	report?: [];

	modalRef?: NgbModalRef;

	constructor() {
	}

}
