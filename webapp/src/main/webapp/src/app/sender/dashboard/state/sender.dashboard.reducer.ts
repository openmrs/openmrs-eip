import {createFeatureSelector, createSelector} from "@ngrx/store";
import {SenderDashboardAction, SenderDashboardActionType} from "./sender.dashboard.actions";
import {ErrorDetails} from "../error-details";

export class SenderDashboardState {
	syncCountByStatus?: Map<string, number>;
	errorDetails?: ErrorDetails;
}

const GET_SENDER_DASHBOARD_FEATURE_STATE = createFeatureSelector<SenderDashboardState>('senderDashboard');

export const GET_SYNC_COUNT_BY_STATUS = createSelector(
	GET_SENDER_DASHBOARD_FEATURE_STATE,
	state => state.syncCountByStatus
);

export const GET_ERROR_DETAILS = createSelector(
	GET_SENDER_DASHBOARD_FEATURE_STATE,
	state => state.errorDetails
);

export function senderDashboardReducer(state = {}, action: SenderDashboardAction) {

	switch (action.type) {

		case SenderDashboardActionType.COUNT_BY_STATUS_RECEIVED:
			return {
				...state,
				syncCountByStatus: action.countByStatus
			};

		case SenderDashboardActionType.ERROR_DETAILS_RECEIVED:
			return {
				...state,
				errorDetails: action.errorDetails
			};

		default:
			return state;
	}

}
