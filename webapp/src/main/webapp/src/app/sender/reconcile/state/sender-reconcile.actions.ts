import {Action} from "@ngrx/store";
import {SenderTableReconcile} from "../sender-table-reconcile";
import {Reconciliation} from "../../../shared/reconciliation";

export enum SenderReconcileActionType {
	LOAD_RECONCILIATION = 'LOAD_RECONCILIATION',
	RECONCILIATION_LOADED = 'RECONCILIATION_LOADED',
	START_RECONCILIATION = 'START_RECONCILIATION',
	LOAD_PROGRESS = 'LOAD_PROGRESS',
	PROGRESS_LOADED = 'PROGRESS_LOADED',
	LOAD_SITE_PROGRESS = 'LOAD_SITE_PROGRESS',
	SITE_PROGRESS_LOADED = 'SITE_PROGRESS_LOADED',
	LOAD_TABLE_RECONCILIATIONS = 'LOAD_TABLE_RECONCILIATIONS',
	TABLE_RECONCILIATIONS_LOADED = 'TABLE_RECONCILIATIONS_LOADED'
}

export class LoadSenderReconciliation implements Action {

	readonly type = SenderReconcileActionType.LOAD_RECONCILIATION;

}

export class SenderReconciliationLoaded implements Action {

	readonly type = SenderReconcileActionType.RECONCILIATION_LOADED;

	constructor(public reconciliation?: Reconciliation) {
	}

}

export class LoadSenderTableReconciliations implements Action {

	readonly type = SenderReconcileActionType.LOAD_TABLE_RECONCILIATIONS;

	constructor(public siteId: number) {
	}

}

export class SenderTableReconciliationsLoaded implements Action {

	readonly type = SenderReconcileActionType.TABLE_RECONCILIATIONS_LOADED;

	constructor(public tableReconciliations?: SenderTableReconcile[]) {
	}

}

export type SenderReconcileAction =
	LoadSenderReconciliation
	| SenderReconciliationLoaded
	| LoadSenderTableReconciliations
	| SenderTableReconciliationsLoaded;
