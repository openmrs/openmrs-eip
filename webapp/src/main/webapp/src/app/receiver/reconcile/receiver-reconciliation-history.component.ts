import {Component, OnDestroy, OnInit} from '@angular/core';
import {Reconciliation} from "../../shared/reconciliation";
import {Subscription} from "rxjs";
import {select, Store} from "@ngrx/store";
import {GET_RECEIVER_HISTORY} from "./state/receiver-reconcile.reducer";
import {LoadReceiverHistory} from "./state/receiver-reconcile.actions";

@Component({
	selector: 'receiver-reconciliation-history',
	templateUrl: './receiver-reconciliation-history.component.html'
})
export class ReceiverReconciliationHistoryComponent implements OnInit, OnDestroy {

	reconciliationHistory?: Reconciliation[];

	historyLoadedSubscription?: Subscription;

	constructor(private store: Store) {
	}

	ngOnInit(): void {
		this.historyLoadedSubscription = this.store.pipe(select(GET_RECEIVER_HISTORY)).subscribe(
			recHistory => {
				this.reconciliationHistory = recHistory;
			}
		);

		this.store.dispatch(new LoadReceiverHistory());
	}

	ngOnDestroy(): void {
		this.historyLoadedSubscription?.unsubscribe();
	}

}
