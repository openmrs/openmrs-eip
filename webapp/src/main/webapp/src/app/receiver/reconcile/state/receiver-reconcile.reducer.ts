import {createFeatureSelector, createSelector} from "@ngrx/store";
import {ReceiverReconcileAction, ReceiverReconcileActionType} from "./receiver-reconcile.actions";
import {ReceiverReconcileProgress} from "../receiver-reconcile-progress";
import {ReceiverTableReconcile} from "../receiver-table-reconcile";
import {Reconciliation} from "../../../shared/reconciliation";

export interface ReceiverReconcileState {
	reconciliation: Reconciliation;
	progress: ReceiverReconcileProgress;
	siteProgress: any;
	tableReconciliations: ReceiverTableReconcile[];
	reconciliationHistory: Reconciliation[];
}

const GET_RECEIVER_RECONCILE_FEATURE_STATE = createFeatureSelector<ReceiverReconcileState>('receiverReconcile');

export const GET_RECEIVER_RECONCILIATION = createSelector(
	GET_RECEIVER_RECONCILE_FEATURE_STATE,
	state => state.reconciliation
);

export const GET_RECEIVER_RECONCILE_PROGRESS = createSelector(
	GET_RECEIVER_RECONCILE_FEATURE_STATE,
	state => state.progress
);

export const GET_SITE_PROGRESS = createSelector(
	GET_RECEIVER_RECONCILE_FEATURE_STATE,
	state => state.siteProgress
);

export const GET_RECEIVER_TABLE_RECONCILES = createSelector(
	GET_RECEIVER_RECONCILE_FEATURE_STATE,
	state => state.tableReconciliations
);

export const GET_RECEIVER_HISTORY = createSelector(
	GET_RECEIVER_RECONCILE_FEATURE_STATE,
	state => state.reconciliationHistory
);

export function receiverReconcileReducer(state = {}, action: ReceiverReconcileAction) {

	switch (action.type) {

		case ReceiverReconcileActionType.RECONCILIATION_LOADED:
			return {
				...state,
				reconciliation: action.reconciliation
			};

		case ReceiverReconcileActionType.PROGRESS_LOADED:
			return {
				...state,
				progress: action.progress
			};

		case ReceiverReconcileActionType.SITE_PROGRESS_LOADED:
			return {
				...state,
				siteProgress: action.siteProgress
			};

		case ReceiverReconcileActionType.TABLE_RECONCILIATIONS_LOADED:
			return {
				...state,
				tableReconciliations: action.tableReconciliations
			};

		case ReceiverReconcileActionType.HISTORY_LOADED:
			return {
				...state,
				reconciliationHistory: action.reconciliationHistory
			};

		default:
			return state;
	}

}
