import {Action} from "@ngrx/store";
import {ReceiverReconciliation} from "../receiver-reconciliation";


export enum ReceiverReconcileActionType {
	LOAD_RECONCILIATION = 'LOAD_RECONCILIATION',
	RECONCILIATION_LOADED = 'RECONCILIATION_LOADED',
	START_RECONCILIATION = 'START_RECONCILIATION'
}

export class LoadReceiverReconciliation implements Action {

	readonly type = ReceiverReconcileActionType.LOAD_RECONCILIATION;

}

export class ReceiverReconciliationLoaded implements Action {

	readonly type = ReceiverReconcileActionType.RECONCILIATION_LOADED;

	constructor(public reconciliation?: ReceiverReconciliation) {
	}

}

export class StartReconciliation implements Action {

	readonly type = ReceiverReconcileActionType.START_RECONCILIATION;

}

export type ReceiverReconcileAction = LoadReceiverReconciliation | ReceiverReconciliationLoaded | StartReconciliation;
