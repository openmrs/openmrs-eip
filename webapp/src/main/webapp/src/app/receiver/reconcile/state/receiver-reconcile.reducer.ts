import {createFeatureSelector, createSelector} from "@ngrx/store";
import {ReceiverReconcileAction, ReceiverReconcileActionType} from "./receiver-reconcile.actions";
import {ReceiverReconcileProgress} from "../receiver-reconcile-progress";
import {ReceiverTableReconcile} from "../receiver-table-reconcile";
import {Reconciliation} from "../../../shared/reconciliation";
import {Site} from "../../site";
import {ReconcileTableSummary} from "../reconcile-table-summary";

export interface ReceiverReconcileState {
	reconciliation: Reconciliation;
	progress: ReceiverReconcileProgress;
	siteProgress: any;
	tableReconciliations: ReceiverTableReconcile[];
	reconciliationHistory: Reconciliation[];
	report: [];
	sites: Site[];
	siteReport: ReconcileTableSummary[];
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

export const GET_REPORT = createSelector(
	GET_RECEIVER_RECONCILE_FEATURE_STATE,
	state => state.report
);

export const GET_SITES = createSelector(
	GET_RECEIVER_RECONCILE_FEATURE_STATE,
	state => state.sites
);

export const GET_SITE_REPORT = createSelector(
	GET_RECEIVER_RECONCILE_FEATURE_STATE,
	state => state.siteReport
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

		case ReceiverReconcileActionType.REPORT_LOADED:
			return {
				...state,
				report: action.report
			};

		case ReceiverReconcileActionType.SITES_LOADED:
			return {
				...state,
				sites: action.sites
			};

		case ReceiverReconcileActionType.SITE_REPORT_LOADED:
			return {
				...state,
				siteReport: action.siteReport
			};

		default:
			return state;
	}

}
