import {createFeatureSelector, createSelector} from "@ngrx/store";
import {Dashboard} from "../dashboard";
import {DashboardAction, DashboardActionType} from "./dashboard.actions";
import {HttpErrorResponse} from "@angular/common/http";

export interface DashboardState {
	dashboard: Dashboard;
	error: HttpErrorResponse;
}

const GET_DASHBOARD_FEATURE_STATE = createFeatureSelector<DashboardState>('dashboard');

export const GET_DASHBOARD = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.dashboard
);

export const GET_DASHBOARD_ERROR = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.error
);

export function dashboardReducer(state = {}, action: DashboardAction) {

	switch (action.type) {

		case DashboardActionType.DASHBOARD_LOADED:
			return {
				...state,
				dashboard: action.dashboard
			};

		case DashboardActionType.LOAD_DASHBOARD_ERROR:
			return {
				...state,
				error: action.error
			};

		default:
			return state;
	}

}
