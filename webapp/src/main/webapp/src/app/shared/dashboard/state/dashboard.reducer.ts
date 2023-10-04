import {createFeatureSelector, createSelector} from "@ngrx/store";
import {Dashboard} from "../dashboard";
import {DashboardAction, DashboardActionType} from "./dashboard.actions";
import {HttpErrorResponse} from "@angular/common/http";
import {QueueData} from "../queue-data/queue-data";

export interface DashboardState {
	dashboard?: Dashboard;
	queueDataMap?: Map<string, QueueData>;
	error: HttpErrorResponse | undefined;
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
	state => state.queueDataMap?.get('sync')?.count
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
			let stateCopy = {
				...state
			};

			let queueDataMap: Map<string, QueueData> | undefined = stateCopy.queueDataMap;
			if (!queueDataMap) {
				queueDataMap = new Map<string, QueueData>();
				stateCopy.queueDataMap = queueDataMap;
			}

			let queueData: QueueData | undefined = queueDataMap.get(action.queueName);
			if (!queueData) {
				queueData = new QueueData();
				queueDataMap.set(action.queueName, queueData);
			}

			queueData.count = action.count;

			return {
				...state,
				queueDataMap: queueDataMap
			};

		default:
			return state;
	}

}
