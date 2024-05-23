import {Action} from "@ngrx/store";
import {ReceiverReconcileProgress} from "../receiver-reconcile-progress";
import {ReceiverTableReconcile} from "../receiver-table-reconcile";
import {Reconciliation} from "../../../shared/reconciliation";
import {Site} from "../../site";
import {ReconcileTableSummary} from "../reconcile-table-summary";

export enum ReceiverReconcileActionType {
	LOAD_RECONCILIATION = 'LOAD_RECEIVER_RECONCILIATION',
	RECONCILIATION_LOADED = 'RECEIVER_RECONCILIATION_LOADED',
	START_RECONCILIATION = 'START_RECONCILIATION',
	LOAD_PROGRESS = 'LOAD_PROGRESS',
	PROGRESS_LOADED = 'PROGRESS_LOADED',
	LOAD_SITE_PROGRESS = 'LOAD_SITE_PROGRESS',
	SITE_PROGRESS_LOADED = 'SITE_PROGRESS_LOADED',
	LOAD_TABLE_RECONCILIATIONS = 'LOAD_TABLE_RECEIVER_RECONCILIATIONS',
	TABLE_RECONCILIATIONS_LOADED = 'RECEIVER_TABLE_RECONCILIATIONS_LOADED',
	LOAD_HISTORY = 'LOAD_RECEIVER_HISTORY',
	HISTORY_LOADED = 'RECEIVER_HISTORY_LOADED',
	LOAD_REPORT = 'LOAD_REPORT',
	REPORT_LOADED = 'REPORT_LOADED',
	LOAD_SITES = 'LOAD_SITES',
	SITES_LOADED = 'SITES_LOADED',
	LOAD_SITE_REPORT = 'LOAD_SITE_REPORT',
	SITE_REPORT_LOADED = 'SITE_REPORT_LOADED'
}

export class LoadReceiverReconciliation implements Action {

	readonly type = ReceiverReconcileActionType.LOAD_RECONCILIATION;

}

export class ReceiverReconciliationLoaded implements Action {

	readonly type = ReceiverReconcileActionType.RECONCILIATION_LOADED;

	constructor(public reconciliation?: Reconciliation) {
	}

}

export class LoadReceiverReconcileProgress implements Action {

	readonly type = ReceiverReconcileActionType.LOAD_PROGRESS;

}

export class ReceiverReconcileProgressLoaded implements Action {

	readonly type = ReceiverReconcileActionType.PROGRESS_LOADED;

	constructor(public progress?: ReceiverReconcileProgress) {
	}

}

export class LoadSiteProgress implements Action {

	readonly type = ReceiverReconcileActionType.LOAD_SITE_PROGRESS;

}

export class SiteProgressLoaded implements Action {

	readonly type = ReceiverReconcileActionType.SITE_PROGRESS_LOADED;

	constructor(public siteProgress?: any) {
	}

}

export class StartReconciliation implements Action {

	readonly type = ReceiverReconcileActionType.START_RECONCILIATION;

}

export class LoadReceiverTableReconciliations implements Action {

	readonly type = ReceiverReconcileActionType.LOAD_TABLE_RECONCILIATIONS;

	constructor(public siteId: number) {
	}

}

export class ReceiverTableReconciliationsLoaded implements Action {

	readonly type = ReceiverReconcileActionType.TABLE_RECONCILIATIONS_LOADED;

	constructor(public tableReconciliations?: ReceiverTableReconcile[]) {
	}

}

export class LoadReceiverHistory implements Action {

	readonly type = ReceiverReconcileActionType.LOAD_HISTORY;

}

export class ReceiverHistoryLoaded implements Action {

	readonly type = ReceiverReconcileActionType.HISTORY_LOADED;

	constructor(public reconciliationHistory?: Reconciliation[]) {
	}

}

export class LoadReport implements Action {

	readonly type = ReceiverReconcileActionType.LOAD_REPORT;

	constructor(public reconcileId?: string) {
	}

}

export class ReportLoaded implements Action {

	readonly type = ReceiverReconcileActionType.REPORT_LOADED;

	constructor(public report?: []) {
	}

}

export class LoadSites implements Action {

	readonly type = ReceiverReconcileActionType.LOAD_SITES;
}

export class SitesLoaded implements Action {

	readonly type = ReceiverReconcileActionType.SITES_LOADED;

	constructor(public sites?: Site[]) {
	}

}

export class LoadSiteReport implements Action {

	readonly type = ReceiverReconcileActionType.LOAD_SITE_REPORT;

	constructor(public reconcileId?: string, public siteId?: string) {
	}

}

export class SiteReportLoaded implements Action {

	readonly type = ReceiverReconcileActionType.SITE_REPORT_LOADED;

	constructor(public siteReport?: ReconcileTableSummary[]) {
	}

}

export type ReceiverReconcileAction =
	LoadReceiverReconciliation
	| ReceiverReconciliationLoaded
	| StartReconciliation
	| LoadReceiverReconcileProgress
	| ReceiverReconcileProgressLoaded
	| LoadSiteProgress
	| SiteProgressLoaded
	| LoadReceiverTableReconciliations
	| ReceiverTableReconciliationsLoaded
	| LoadReceiverHistory
	| ReceiverHistoryLoaded
	| LoadReport
	| ReportLoaded
	| LoadSites
	| SitesLoaded
	| LoadSiteReport
	| SiteReportLoaded;
