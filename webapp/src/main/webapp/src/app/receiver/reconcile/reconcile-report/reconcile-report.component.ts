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
	}

	filterBySite(site?: Site): void {
		this.activeSite = site;
		this.store.dispatch(new LoadSiteReport(this.reconcileId, this.activeSite?.identifier))
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
	}

	ngOnDestroy(): void {
		this.siteReportSubscription?.unsubscribe();
	}

}
