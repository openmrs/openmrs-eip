import {Action} from "@ngrx/store";
import {ReceiverReconciliation} from "../receiver-reconciliation";
import {ReceiverReconcileProgress} from "../receiver-reconcile-progress";

export enum ReceiverReconcileActionType {
	LOAD_RECONCILIATION = 'LOAD_RECONCILIATION',
	RECONCILIATION_LOADED = 'RECONCILIATION_LOADED',
	START_RECONCILIATION = 'START_RECONCILIATION',
	LOAD_PROGRESS = 'LOAD_PROGRESS',
	PROGRESS_LOADED = 'PROGRESS_LOADED',
	LOAD_SITE_PROGRESS = 'LOAD_SITE_PROGRESS',
	SITE_PROGRESS_LOADED = 'SITE_PROGRESS_LOADED'
}

export class LoadReceiverReconciliation implements Action {

	readonly type = ReceiverReconcileActionType.LOAD_RECONCILIATION;

}

export class ReceiverReconciliationLoaded implements Action {

	readonly type = ReceiverReconcileActionType.RECONCILIATION_LOADED;

	constructor(public reconciliation?: ReceiverReconciliation) {
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

export type ReceiverReconcileAction =
	LoadReceiverReconciliation
	| ReceiverReconciliationLoaded
	| StartReconciliation
	| LoadReceiverReconcileProgress
	| ReceiverReconcileProgressLoaded
	| LoadSiteProgress
	| SiteProgressLoaded;
