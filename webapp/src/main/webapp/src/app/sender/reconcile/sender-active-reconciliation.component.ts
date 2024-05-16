import {Component, OnDestroy, OnInit} from '@angular/core';
import {ReconcileStatus} from "../../shared/reconcile-status.enum";
import {Reconciliation} from "../../shared/reconciliation";
import {SenderTableReconcile} from "./sender-table-reconcile";
import {Subscription} from "rxjs";
import {select, Store} from "@ngrx/store";
import {GET_SENDER_RECONCILIATION, GET_SENDER_TABLE_RECONCILES} from "./state/sender-reconcile.reducer";
import {
	LoadSenderReconciliation,
	LoadSenderTableReconciliations,
	SenderReconciliationLoaded
} from "./state/sender-reconcile.actions";

@Component({
	selector: 'sender-active-reconciliation',
	templateUrl: './sender-active-reconciliation.component.html'
})
export class SenderActiveReconciliationComponent implements OnInit, OnDestroy {

	ReconcileStatusEnum = ReconcileStatus;

	reconciliation?: Reconciliation;

	tableReconciliations?: SenderTableReconcile[];

	recLoadedSubscription?: Subscription;

	loadedTableRecsSubscription?: Subscription;

	constructor(private store: Store) {
	}

	ngOnInit(): void {
		this.recLoadedSubscription = this.store.pipe(select(GET_SENDER_RECONCILIATION)).subscribe(
			reconciliation => {
				this.reconciliation = reconciliation;
				if (reconciliation?.status == ReconcileStatus.PROCESSING) {
					this.store.dispatch(new LoadSenderTableReconciliations());
				}
			}
		);

		this.loadedTableRecsSubscription = this.store.pipe(select(GET_SENDER_TABLE_RECONCILES)).subscribe(
			tableRecs => {
				this.tableReconciliations = tableRecs;
			}
		);

		this.store.dispatch(new LoadSenderReconciliation());
	}

	getStatusDisplay(): string {
		let display: string = '';
		switch (this.reconciliation?.status) {
			case ReconcileStatus.NEW:
				display = $localize`:@@common-pending:Pending`;
				break;
			case ReconcileStatus.PROCESSING:
				display = $localize`:@@common-processing:Processing`;
				break;
			case ReconcileStatus.POST_PROCESSING:
				display = $localize`:@@sender-sending-deleted-rows:Sending Deleted Rows`;
				break;
		}

		return display;
	}

	ngOnDestroy(): void {
		this.recLoadedSubscription?.unsubscribe();
		this.loadedTableRecsSubscription?.unsubscribe();
		this.reset();
	}

	reset(): void {
		//Reset the ngrx store
		this.store.dispatch(new SenderReconciliationLoaded());
	}

}
