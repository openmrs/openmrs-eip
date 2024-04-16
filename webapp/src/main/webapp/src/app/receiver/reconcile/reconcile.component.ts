import {Component, OnDestroy, OnInit} from '@angular/core';
import {Store} from "@ngrx/store";
import {Subscription} from "rxjs";
import {ReconcileService} from "./reconcile.service";
import {LoadReceiverReconciliation} from "./state/reconcile.actions";

@Component({
	selector: 'receiver-reconcile',
	templateUrl: './reconcile.component.html'
})
export class ReconcileComponent implements OnInit, OnDestroy {

	loadedSubscription?: Subscription;

	constructor(
		private service: ReconcileService,
		private store: Store) {
	}

	ngOnInit(): void {
		this.store.dispatch(new LoadReceiverReconciliation());
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
	}

}
