import {ReceiverReconciliation} from "../receiver-reconciliation";
import {createFeatureSelector, createSelector} from "@ngrx/store";
import {ReceiverReconcileAction, ReceiverReconcileActionType} from "./receiver-reconcile.actions";
import {ReceiverReconcileProgress} from "../receiver-reconcile-progress";

export interface ReceiverReconcileState {
	reconciliation: ReceiverReconciliation;
	progress: ReceiverReconcileProgress;
	siteProgress: any;
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

		default:
			return state;
	}

}
