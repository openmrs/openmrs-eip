import {createFeatureSelector, createSelector} from "@ngrx/store";
import {SenderDashboardAction, SenderDashboardActionType} from "./sender.dashboard.actions";

export class SenderDashboardState {
	syncCountByStatus?: Map<string, number>;
}

const GET_SENDER_DASHBOARD_FEATURE_STATE = createFeatureSelector<SenderDashboardState>('senderDashboard');

export const GET_SYNC_COUNT_BY_STATUS = createSelector(
	GET_SENDER_DASHBOARD_FEATURE_STATE,
	state => state.syncCountByStatus
);

export function senderDashboardReducer(state: {}, action: SenderDashboardAction) {

	switch (action.type) {

		case SenderDashboardActionType.COUNT_BY_STATUS_RECEIVED:
			return {
				...state,
				syncCountByStatus: action.countByStatus
			};

		default:
			return state;
	}

}
