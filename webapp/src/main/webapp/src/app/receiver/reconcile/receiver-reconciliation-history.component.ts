import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Reconciliation} from "../../shared/reconciliation";
import {Subscription} from "rxjs";
import {select, Store} from "@ngrx/store";
import {GET_RECEIVER_HISTORY, GET_REPORT} from "./state/receiver-reconcile.reducer";
import {LoadReceiverHistory, LoadReport, ReportLoaded} from "./state/receiver-reconcile.actions";
import {NgbModal, NgbModalOptions, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ReconcileReportComponent} from "./reconcile-report/reconcile-report.component";

@Component({
	selector: 'receiver-reconciliation-history',
	templateUrl: './receiver-reconciliation-history.component.html'
})
export class ReceiverReconciliationHistoryComponent implements OnInit, OnDestroy {

	reconciliationHistory?: Reconciliation[];

	report?: []

	modalRef?: NgbModalRef;

	@ViewChild('reportTemplate')
	reportRef?: ElementRef;

	historyLoadedSubscription?: Subscription;

	reportLoadedSubscription?: Subscription;

	constructor(
		private store: Store,
		private modalService: NgbModal) {
	}

	ngOnInit(): void {
		this.historyLoadedSubscription = this.store.pipe(select(GET_RECEIVER_HISTORY)).subscribe(
			recHistory => {
				this.reconciliationHistory = recHistory;
			}
		);

		this.reportLoadedSubscription = this.store.pipe(select(GET_REPORT)).subscribe(
			report => {
				this.report = report;
				if (this.report) {
					this.showReport();
				}
			}
		);

		this.store.dispatch(new LoadReceiverHistory());
	}

	getReport(reconciliationId?: string): void {
		this.store.dispatch(new LoadReport(reconciliationId));
	}

	showReport(): void {
		const dialogConfig: NgbModalOptions = {
			size: 'xl',
			scrollable: true
		}

		this.modalRef = this.modalService.open(ReconcileReportComponent, dialogConfig);
		this.modalRef.componentInstance.report = this.report;
		this.modalRef.closed.subscribe(() => {
			//Clear
			this.store.dispatch(new ReportLoaded());
		});
	}

	ngOnDestroy(): void {
		this.historyLoadedSubscription?.unsubscribe();
		this.reportLoadedSubscription?.unsubscribe();
	}

}
