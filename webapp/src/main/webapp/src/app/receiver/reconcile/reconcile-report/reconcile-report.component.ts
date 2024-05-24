import {Component, OnDestroy, OnInit} from '@angular/core';
import {NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {Site} from "../../site";
import {Subscription} from "rxjs";
import {select, Store} from "@ngrx/store";
import {GET_SITE_REPORT} from "../state/receiver-reconcile.reducer";
import {ReconcileTableSummary} from "../reconcile-table-summary";
import {LoadSiteReport} from "../state/receiver-reconcile.actions";

@Component({
	selector: 'reconcile-report',
	templateUrl: './reconcile-report.component.html'
})
export class ReconcileReportComponent implements OnInit, OnDestroy {

	selectLabel?: string;

	report?: any[];

	cleanReport?: any[];

	fullCleanReport?: any[];

	modalRef?: NgbModalRef;

	sites?: Site[];

	reconcileId?: string;

	activeSite?: Site;

	siteReportSubscription?: Subscription;

	constructor(private store: Store) {
	}

	ngOnInit(): void {
		this.siteReportSubscription = this.store.pipe(select(GET_SITE_REPORT)).subscribe(
			siteReport => {
				if (siteReport) {
					this.updateReport(siteReport)
				}
			}
		);

		this.selectLabel = $localize`:@@common-select:Select`;
		this.setCleanReport();
		this.fullCleanReport = this.cleanReport;
	}

	setCleanReport(): void {
		if (this.report) {
			let cleanRows: any[] = new Array();
			for (let i = 0; i < this.report.length; i++) {
				let row: any[] = this.report[i];
				if (row[1] == 0 && row[2] == 0 && row[3] == 0 && row[4] == 0 && row[5] == 0 && row[6] == 0) {
					continue;
				}

				let cleanRow: any[] = [];
				for (let j = 0; j < row.length; j++) {
					cleanRow.push(row[j] == 0 ? '' : row[j]);
				}

				cleanRows.push(cleanRow);
			}

			this.cleanReport = cleanRows;
		}
	}

	filterBySite(site?: Site): void {
		this.activeSite = site;
		this.store.dispatch(new LoadSiteReport(this.reconcileId, this.activeSite?.identifier))
	}

	clearFilter(): void {
		this.cleanReport = this.fullCleanReport;
		this.activeSite = undefined;
	}

	updateReport(siteReport: ReconcileTableSummary[]): void {
		let rows: any[] = [];
		siteReport.forEach(r => {
			let row: any[] = [];
			row.push(r.tableName);
			row.push(r.missingCount);
			row.push(r.missingSyncCount);
			row.push(r.missingErrorCount);
			row.push(r.undeletedCount);
			row.push(r.undeletedSyncCount);
			row.push(r.undeletedErrorCount);
			rows.push(row);
		});

		this.report = rows;
		this.setCleanReport();
	}

	ngOnDestroy(): void {
		this.siteReportSubscription?.unsubscribe();
	}

}
