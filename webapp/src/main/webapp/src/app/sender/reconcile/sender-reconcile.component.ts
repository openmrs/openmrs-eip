import {Component, OnInit} from '@angular/core';
import {Reconciliation} from "../../shared/reconciliation";

@Component({
	selector: 'sender-reconcile',
	templateUrl: './sender-reconcile.component.html'
})
export class SenderReconcileComponent implements OnInit {

	reconciliation?: Reconciliation;

	constructor() {
	}

	ngOnInit(): void {
	}

}
