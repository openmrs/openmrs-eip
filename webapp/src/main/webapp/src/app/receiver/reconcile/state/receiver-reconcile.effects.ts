import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {map, mergeMap, switchMap} from "rxjs/operators";
import {
	ReceiverHistoryLoaded,
	ReceiverReconcileActionType,
	ReceiverReconcileProgressLoaded,
	ReceiverReconciliationLoaded,
	ReceiverTableReconciliationsLoaded,
	ReportLoaded,
	SiteProgressLoaded,
	SiteReportLoaded,
	SitesLoaded
} from "./receiver-reconcile.actions";
import {ReceiverReconcileService} from "../receiver-reconcile.service";
import {ReceiverService} from "../../receiver.service";

@Injectable()
export class ReceiverReconcileEffects {

	constructor(
		private actions$: Actions,
		private service: ReceiverReconcileService,
		private receiverService: ReceiverService) {
	}

	getReconciliation = createEffect(() =>
		this.actions$.pipe(
			ofType(ReceiverReconcileActionType.LOAD_RECONCILIATION),
			switchMap(() => this.service.getReconciliation()
				.pipe(
					map(reconciliation => new ReceiverReconciliationLoaded(reconciliation))
				)
			)
		)
	);

	startReconciliation = createEffect(() =>
		this.actions$.pipe(
			ofType(ReceiverReconcileActionType.START_RECONCILIATION),
			switchMap(() => this.service.startReconciliation()
				.pipe(
					map(reconciliation => new ReceiverReconciliationLoaded(reconciliation))
				)
			)
		)
	);

	getReconciliationProgress = createEffect(() =>
		this.actions$.pipe(
			ofType(ReceiverReconcileActionType.LOAD_PROGRESS),
			switchMap(() => this.service.getReconciliationProgress()
				.pipe(
					map(progress => new ReceiverReconcileProgressLoaded(progress))
				)
			)
		)
	);

	getSiteProgress = createEffect(() =>
		this.actions$.pipe(
			ofType(ReceiverReconcileActionType.LOAD_SITE_PROGRESS),
			switchMap(() => this.service.getSiteProgress()
				.pipe(
					map(siteProgress => new SiteProgressLoaded(siteProgress))
				)
			)
		)
	);

	getTableReconciliations = createEffect(() =>
		this.actions$.pipe(
			ofType(ReceiverReconcileActionType.LOAD_TABLE_RECONCILIATIONS),
			mergeMap(action => this.service.getIncompleteTableReconciliations(action['siteId'])
				.pipe(
					map(tableRecs => new ReceiverTableReconciliationsLoaded(tableRecs))
				)
			)
		)
	);

	getHistory = createEffect(() =>
		this.actions$.pipe(
			ofType(ReceiverReconcileActionType.LOAD_HISTORY),
			switchMap(() => this.service.getHistory()
				.pipe(
					map(recHistory => new ReceiverHistoryLoaded(recHistory))
				)
			)
		)
	);

	getReport = createEffect(() =>
		this.actions$.pipe(
			ofType(ReceiverReconcileActionType.LOAD_REPORT),
			switchMap(action => this.service.getReport(action['reconcileId'])
				.pipe(
					map(report => new ReportLoaded(report))
				)
			)
		)
	);

	getSiteReport = createEffect(() =>
		this.actions$.pipe(
			ofType(ReceiverReconcileActionType.LOAD_SITE_REPORT),
			switchMap(action => this.service.getTableSummariesBySite(action['reconcileId'], action['siteId'])
				.pipe(
					map(siteReport => new SiteReportLoaded(siteReport))
				)
			)
		)
	);

	getSites = createEffect(() =>
		this.actions$.pipe(
			ofType(ReceiverReconcileActionType.LOAD_SITES),
			switchMap(() => this.receiverService.getSites()
				.pipe(
					map(sites => new SitesLoaded(sites))
				)
			)
		)
	);

}
