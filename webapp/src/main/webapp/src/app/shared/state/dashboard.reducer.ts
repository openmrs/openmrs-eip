import {createFeatureSelector, createSelector} from "@ngrx/store";
import {Dashboard} from "../dashboard";
import {DashboardAction, DashboardActionType} from "./dashboard.actions";

export interface DashboardState {
	dashboard: Dashboard;
}

const GET_DASHBOARD_FEATURE_STATE = createFeatureSelector<DashboardState>('dashboard');


export const GET_DASHBOARD = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.dashboard
);

export function dashboardReducer(state = {}, action: DashboardAction) {

	switch (action.type) {

		case DashboardActionType.DASHBOARD_LOADED:
			return {
				...state,
				dashboard: action.dashboard
			};

		default:
			return state;
	}

}
