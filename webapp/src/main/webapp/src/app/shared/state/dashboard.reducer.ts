import {createFeatureSelector, createSelector} from "@ngrx/store";
import {Dashboard} from "../dashboard";
import {DashboardAction, DashboardActionType} from "./dashboard.actions";
import {HttpErrorResponse} from "@angular/common/http";
import {QueueData} from "../dashboard/queue-data/queue-data";

export interface DashboardState {
	dashboard?: Dashboard;
	queueData?: Map<string, any>;
	error: HttpErrorResponse | any;
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

export const GET_SYNC_COUNT = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueData?.get('sync').count
);

export function dashboardReducer(state: DashboardState = {error: undefined}, action: DashboardAction) {

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

		case DashboardActionType.COUNT_RECEIVED:
			let newState = {
				...state
			};
			//queueData: new Map<string, any>()
			let queueData: QueueData | undefined = newState.queueData?.get(action.queueName);

			//if(!queueData){
			queueData = new QueueData();
			//}
			newState.queueData?.set(action.queueName, queueData);

			queueData.count = action.count;
			console.log(newState.queueData?.get(action.queueName));
			return {
				...state,
				queueData: queueData
			};

		default:
			return state;
	}

}
