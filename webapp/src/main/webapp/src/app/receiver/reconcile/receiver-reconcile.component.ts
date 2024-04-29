import {Component, OnDestroy, OnInit} from '@angular/core';
import {select, Store} from "@ngrx/store";
import {Subscription} from "rxjs";
import {
	GET_RECEIVER_RECONCILE_PROGRESS,
	GET_RECEIVER_RECONCILIATION,
	GET_SITE_PROGRESS
} from "./state/receiver-reconcile.reducer";
import {
	LoadReceiverReconcileProgress,
	LoadReceiverReconciliation,
	LoadSiteProgress,
	StartReconciliation
} from "./state/receiver-reconcile.actions";
import {ReceiverReconcileService} from "./receiver-reconcile.service";
import {ReceiverReconcileProgress} from "./receiver-reconcile-progress";
import {ReceiverTableReconcile} from "./receiver-table-reconcile";
import {Reconciliation} from "../../shared/reconciliation";
import {ReconcileStatus} from "../../shared/reconcile-status.enum";

@Component({
	selector: 'receiver-reconcile',
	templateUrl: './receiver-reconcile.component.html'
})
export class ReceiverReconcileComponent implements OnInit, OnDestroy {

	ReconcileStatusEnum = ReconcileStatus;

	reconciliation?: Reconciliation;

	progress?: ReceiverReconcileProgress;

	siteProgress?: any;

	tableReconciliations?: ReceiverTableReconcile[];

	loadedSubscription?: Subscription;

	loadedProgressSubscription?: Subscription;

	loadedSiteProgressSubscription?: Subscription;

	constructor(
		private service: ReceiverReconcileService,
		private store: Store) {
	}

	ngOnInit(): void {
		this.loadedSubscription = this.store.pipe(select(GET_RECEIVER_RECONCILIATION)).subscribe(
			reconciliation => {
				this.reconciliation = reconciliation;
				if (this.reconciliation && this.reconciliation.status == ReconcileStatus.PROCESSING) {
					this.store.dispatch(new LoadReceiverReconcileProgress());
				}
			}
		);

		this.loadedProgressSubscription = this.store.pipe(select(GET_RECEIVER_RECONCILE_PROGRESS)).subscribe(
			progress => {
				this.progress = progress;
			}
		);

		this.loadedSiteProgressSubscription = this.store.pipe(select(GET_SITE_PROGRESS)).subscribe(
			siteProgress => {
				this.siteProgress = siteProgress;
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
			case ReconcileStatus.NEW:
				display = $localize`:@@common-pending:Pending`;
				break;
			case ReconcileStatus.PROCESSING:
				display = $localize`:@@common-processing:Processing`;
				break;
			case ReconcileStatus.POST_PROCESSING:
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

	getTableCount(): number {
		let count: number = 0;
		if (this.progress && this.progress.tableCount) {
			count = this.progress.tableCount;
		}
		return count;
	}

	showSiteDetails(): void {
		this.store.dispatch(new LoadSiteProgress());
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
		this.loadedProgressSubscription?.unsubscribe();
		this.loadedSiteProgressSubscription?.unsubscribe();
	}

}
