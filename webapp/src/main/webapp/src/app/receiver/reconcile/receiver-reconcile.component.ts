import {Component, OnDestroy, OnInit} from '@angular/core';
import {select, Store} from "@ngrx/store";
import {Subscription} from "rxjs";
import {ReceiverReconciliation} from "./receiver-reconciliation";
import {GET_RECEIVER_RECONCILE_PROGRESS, GET_RECEIVER_RECONCILIATION} from "./state/receiver-reconcile.reducer";
import {
	LoadReceiverReconcileProgress,
	LoadReceiverReconciliation,
	StartReconciliation
} from "./state/receiver-reconcile.actions";
import {ReceiverReconcileStatus} from "./receiver-reconcile-status.enum";
import {ReceiverReconcileService} from "./receiver-reconcile.service";
import {ReceiverReconcileProgress} from "./receiver-reconcile-progress";

@Component({
	selector: 'receiver-reconcile',
	templateUrl: './receiver-reconcile.component.html'
})
export class ReceiverReconcileComponent implements OnInit, OnDestroy {

	ReceiverReconcileStatusEnum = ReceiverReconcileStatus;

	reconciliation?: ReceiverReconciliation;

	progress?: ReceiverReconcileProgress;

	loadedSubscription?: Subscription;

	loadedProgressSubscription?: Subscription;

	constructor(
		private service: ReceiverReconcileService,
		private store: Store) {
	}

	ngOnInit(): void {
		this.loadedSubscription = this.store.pipe(select(GET_RECEIVER_RECONCILIATION)).subscribe(
			reconciliation => {
				this.reconciliation = reconciliation;
				if (this.reconciliation && this.reconciliation.status == ReceiverReconcileStatus.PROCESSING) {
					this.store.dispatch(new LoadReceiverReconcileProgress());
				}
			}
		);

		this.loadedProgressSubscription = this.store.pipe(select(GET_RECEIVER_RECONCILE_PROGRESS)).subscribe(
			progress => {
				this.progress = progress;
			}
		);

		this.store.dispatch(new LoadReceiverReconciliation());
	}

	start(): void {
		this.store.dispatch(new StartReconciliation());
	}

	displayStatus(): string {
		let display: string = '';
		switch (this.reconciliation?.status) {
			case ReceiverReconcileStatus.NEW:
				display = $localize`:@@common-pending:Pending`;
				break;
			case ReceiverReconcileStatus.PROCESSING:
				display = $localize`:@@common-processing:Processing`;
				break;
			case ReceiverReconcileStatus.POST_PROCESSING:
				display = $localize`:@@reconcile-generating-report:Generating Report`;
				break;
		}

		return display;
	}

	getCompletedSites(): number {
		let count: number = 0;
		if (this.progress && this.progress.completedSiteCount) {
			count = this.progress.completedSiteCount;
		}
		return count;
	}

	getTotalCount(): number {
		let count: number = 0;
		if (this.progress && this.progress.totalCount) {
			count = this.progress.totalCount;
		}
		return count;
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
		this.loadedProgressSubscription?.unsubscribe();
	}

}
