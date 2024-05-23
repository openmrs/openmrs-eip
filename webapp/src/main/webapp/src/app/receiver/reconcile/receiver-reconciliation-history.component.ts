import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Reconciliation} from "../../shared/reconciliation";
import {Subscription} from "rxjs";
import {select, Store} from "@ngrx/store";
import {GET_RECEIVER_HISTORY, GET_REPORT, GET_SITES} from "./state/receiver-reconcile.reducer";
import {
	LoadReceiverHistory,
	LoadReport,
	LoadSites,
	ReportLoaded,
	SiteReportLoaded,
	SitesLoaded
} from "./state/receiver-reconcile.actions";
import {NgbModal, NgbModalOptions, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ReconcileReportComponent} from "./reconcile-report/reconcile-report.component";
import {Site} from "../site";

@Component({
	selector: 'receiver-reconciliation-history',
	templateUrl: './receiver-reconciliation-history.component.html'
})
export class ReceiverReconciliationHistoryComponent implements OnInit, OnDestroy {

	reconciliationHistory?: Reconciliation[];

	report?: any[];

	sites?: Site[];

	activeReconcileId?: string;

	modalRef?: NgbModalRef;

	@ViewChild('reportTemplate')
	reportRef?: ElementRef;

	historyLoadedSubscription?: Subscription;

	reportLoadedSubscription?: Subscription;

	sitesLoadedSubscription?: Subscription;

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
					this.updateAndShowReport(report);
				}
			}
		);

		this.sitesLoadedSubscription = this.store.pipe(select(GET_SITES)).subscribe(
			sites => {
				this.sites = sites;
				if (sites) {
					this.store.dispatch(new LoadReport(this.activeReconcileId));
				}
			}
		);

		this.store.dispatch(new LoadReceiverHistory());
	}

	getReport(reconciliationId?: string): void {
		this.activeReconcileId = reconciliationId;
		this.store.dispatch(new LoadSites());
	}

	updateAndShowReport(report: []): void {
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
		this.modalRef.componentInstance.modalRef = this.modalRef;
		this.modalRef.componentInstance.sites = this.sites;
		this.modalRef.componentInstance.reconcileId = this.activeReconcileId;
		this.modalRef.closed.subscribe(() => {
			//Reset
			this.activeReconcileId = undefined;
			this.store.dispatch(new SitesLoaded());
			this.store.dispatch(new SiteReportLoaded());
			this.store.dispatch(new ReportLoaded());
		});
	}

	ngOnDestroy(): void {
		this.historyLoadedSubscription?.unsubscribe();
		this.reportLoadedSubscription?.unsubscribe();
		this.sitesLoadedSubscription?.unsubscribe();
	}

}
