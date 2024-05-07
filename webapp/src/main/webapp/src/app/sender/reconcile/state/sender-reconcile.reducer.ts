import {createFeatureSelector, createSelector} from "@ngrx/store";
import {SenderReconcileAction, SenderReconcileActionType} from "./sender-reconcile.actions";
import {SenderTableReconcile} from "../sender-table-reconcile";
import {Reconciliation} from "../../../shared/reconciliation";

export interface SenderReconcileState {
	reconciliation: Reconciliation;
	tableReconciliations: SenderTableReconcile[];
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

		default:
			return state;
	}

}
