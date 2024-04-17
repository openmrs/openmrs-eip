import {Component, OnDestroy, OnInit} from '@angular/core';
import {select, Store} from "@ngrx/store";
import {Subscription} from "rxjs";
import {ReconcileService} from "./reconcile.service";
import {ReceiverReconciliation} from "./receiver-reconciliation";
import {GET_RECEIVER_RECONCILIATION} from "./state/receiver-reconcile.reducer";
import {LoadReceiverReconciliation} from "./state/receiver-reconcile.actions";
import {ReceiverReconcileStatus} from "./receiver-reconcile-status.enum";

@Component({
	selector: 'receiver-reconcile',
	templateUrl: './reconcile.component.html'
})
export class ReconcileComponent implements OnInit, OnDestroy {

	ReceiverReconcileStatusEnum = ReceiverReconcileStatus;

	reconciliation?: ReceiverReconciliation;

	loadedSubscription?: Subscription;

	constructor(
		private service: ReconcileService,
		private store: Store) {
	}

	ngOnInit(): void {
		this.loadedSubscription = this.store.pipe(select(GET_RECEIVER_RECONCILIATION)).subscribe(
			reconciliation => {
				this.reconciliation = reconciliation;
			}
		);

		this.store.dispatch(new LoadReceiverReconciliation());
	}

	start():void {

	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
	}

}
