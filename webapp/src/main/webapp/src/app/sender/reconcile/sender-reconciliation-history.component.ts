import {Component, OnDestroy, OnInit} from '@angular/core';
import {Reconciliation} from "../../shared/reconciliation";
import {Subscription} from "rxjs";
import {select, Store} from "@ngrx/store";
import {GET_SENDER_HISTORY} from "./state/sender-reconcile.reducer";
import {LoadSenderHistory} from "./state/sender-reconcile.actions";

@Component({
	selector: 'sender-reconciliation-history',
	templateUrl: './sender-reconciliation-history.component.html'
})
export class SenderReconciliationHistoryComponent implements OnInit, OnDestroy {

	reconciliationHistory?: Reconciliation[];

	historyLoadedSubscription?: Subscription;

	constructor(private store: Store) {
	}

	ngOnInit(): void {
		this.historyLoadedSubscription = this.store.pipe(select(GET_SENDER_HISTORY)).subscribe(
			recHistory => {
				this.reconciliationHistory = recHistory;
			}
		);

		this.store.dispatch(new LoadSenderHistory());
	}

	ngOnDestroy(): void {
		this.historyLoadedSubscription?.unsubscribe();
	}

}
