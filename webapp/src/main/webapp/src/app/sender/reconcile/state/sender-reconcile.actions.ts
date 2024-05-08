import {Action} from "@ngrx/store";
import {SenderTableReconcile} from "../sender-table-reconcile";
import {Reconciliation} from "../../../shared/reconciliation";

export enum SenderReconcileActionType {
	LOAD_RECONCILIATION = 'LOAD_SENDER_RECONCILIATION',
	RECONCILIATION_LOADED = 'SENDER_RECONCILIATION_LOADED',
	LOAD_TABLE_RECONCILIATIONS = 'LOAD_TABLE_SENDER_RECONCILIATIONS',
	TABLE_RECONCILIATIONS_LOADED = 'SENDER_TABLE_RECONCILIATIONS_LOADED'
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
