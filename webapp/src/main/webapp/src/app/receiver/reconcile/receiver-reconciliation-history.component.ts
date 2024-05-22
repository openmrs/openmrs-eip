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

	report?: any[]

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
				if (report) {
					this.setAndDisplayReport(report);
				}
			}
		);

		this.store.dispatch(new LoadReceiverHistory());
	}

	getReport(reconciliationId?: string): void {
		this.store.dispatch(new LoadReport(reconciliationId));
	}

	setAndDisplayReport(report: []): void {
		let cleanRows: any[] = new Array();
		for (let i = 0; i < report.length; i++) {
			let row: any[] = report[i];
			if (row[1] == 0 && row[2] == 0 && row[3] == 0 && row[4] == 0 && row[5] == 0 && row[6] == 0) {
				continue;
			}

			let cleanRow: any[] = [];
			for (let j = 0; j < row.length; j++) {
				cleanRow.push(row[j] == 0 ? '' : row[j]);
			}

			cleanRows.push(cleanRow);
		}

		this.report = cleanRows;
		this.showReport();
	}

	showReport(): void {
		const dialogConfig: NgbModalOptions = {
			size: 'xl',
			centered: true,
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
