import {createFeatureSelector, createSelector} from "@ngrx/store";
import {SenderReconcileAction, SenderReconcileActionType} from "./sender-reconcile.actions";
import {SenderTableReconcile} from "../sender-table-reconcile";
import {Reconciliation} from "../../../shared/reconciliation";

export interface SenderReconcileState {
	reconciliation: Reconciliation;
	tableReconciliations: SenderTableReconcile[];
	reconciliationHistory: Reconciliation[];
}

const GET_SENDER_RECONCILE_FEATURE_STATE = createFeatureSelector<SenderReconcileState>('senderReconcile');

export const GET_SENDER_RECONCILIATION = createSelector(
	GET_SENDER_RECONCILE_FEATURE_STATE,
	state => state.reconciliation
);

export const GET_SENDER_TABLE_RECONCILES = createSelector(
	GET_SENDER_RECONCILE_FEATURE_STATE,
	state => state.tableReconciliations
);

export const GET_SENDER_HISTORY = createSelector(
	GET_SENDER_RECONCILE_FEATURE_STATE,
	state => state.reconciliationHistory
);

export function senderReconcileReducer(state = {}, action: SenderReconcileAction) {

	switch (action.type) {

		case SenderReconcileActionType.RECONCILIATION_LOADED:
			return {
				...state,
				reconciliation: action.reconciliation
			};

		case SenderReconcileActionType.TABLE_RECONCILIATIONS_LOADED:
			return {
				...state,
				tableReconciliations: action.tableReconciliations
			};

		case SenderReconcileActionType.HISTORY_LOADED:
			return {
				...state,
				reconciliationHistory: action.reconciliationHistory
			};

		default:
			return state;
	}

}
