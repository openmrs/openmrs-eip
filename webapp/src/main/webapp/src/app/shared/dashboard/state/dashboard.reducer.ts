import {createFeatureSelector, createSelector} from "@ngrx/store";
import {Dashboard} from "../dashboard";
import {DashboardAction, DashboardActionType} from "./dashboard.actions";
import {HttpErrorResponse} from "@angular/common/http";
import {QueueData} from "../queue-data/queue-data";

export interface DashboardState {
	dashboard?: Dashboard;
	queueAndDataMap?: Map<string, QueueData>;
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
	state => state.queueAndDataMap?.get('sync')?.count
);

export const GET_SYNC_CATEGORIES = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('sync')?.categories
);

export const GET_SYNCED_COUNT = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('synced')?.count
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

		case DashboardActionType.QUEUE_COUNT_RECEIVED:
			let stateCopyForCount = {
				...state
			};

			let queueAndDataMapForCount: Map<string, QueueData> | undefined = stateCopyForCount.queueAndDataMap;
			if (!queueAndDataMapForCount) {
				queueAndDataMapForCount = new Map<string, QueueData>();
				stateCopyForCount.queueAndDataMap = queueAndDataMapForCount;
			}

			let queueDataForCount: QueueData | undefined = queueAndDataMapForCount.get(action.queueName);
			if (!queueDataForCount) {
				queueDataForCount = new QueueData();
				queueAndDataMapForCount.set(action.queueName, queueDataForCount);
			}

			queueDataForCount.count = action.count;

			return {
				...state,
				queueAndDataMap: queueAndDataMapForCount
			};

		case DashboardActionType.QUEUE_CATEGORIES_RECEIVED:
			let stateCopyForCats = {
				...state
			};

			let queueDataMapForCats: Map<string, QueueData> | undefined = stateCopyForCats.queueAndDataMap;
			if (!queueDataMapForCats) {
				queueDataMapForCats = new Map<string, QueueData>();
				stateCopyForCats.queueAndDataMap = queueDataMapForCats;
			}

			let queueDataForCats: QueueData | undefined = queueDataMapForCats.get(action.queueName);
			if (!queueDataForCats) {
				queueDataForCats = new QueueData();
				queueDataMapForCats.set(action.queueName, queueDataForCats);
			}

			queueDataForCats.categories = action.categories;

			return {
				...state,
				queueAndDataMap: queueDataMapForCats
			};

		default:
			return state;
	}

}
