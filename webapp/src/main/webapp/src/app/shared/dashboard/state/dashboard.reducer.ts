import {createFeatureSelector, createSelector} from "@ngrx/store";
import {Dashboard} from "../dashboard";
import {DashboardAction, DashboardActionType} from "./dashboard.actions";
import {HttpErrorResponse} from "@angular/common/http";
import {QueueData} from "../queue-data/queue-data";
import {SyncOperation} from "../../sync-operation.enum";

export class DashboardState {
	dashboard?: Dashboard;
	queueAndDataMap?: Map<string, QueueData> = new Map<string, QueueData>([
		['sync', new QueueData()],
		['synced', new QueueData()]
	]);
	error?: HttpErrorResponse;
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

export const GET_SYNC_CATEGORY_COUNTS = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('sync')?.categoryAndCounts
);

export const GET_SYNCED_COUNT = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('synced')?.count
);

export function dashboardReducer(state: DashboardState = new DashboardState(), action: DashboardAction) {

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
			let newStateForCount = {
				...state
			};

			let queueDataForCount: QueueData | undefined = newStateForCount.queueAndDataMap?.get(action.queueName);
			if (queueDataForCount) {
				queueDataForCount.count = action.count;
			}

			return newStateForCount;

		case DashboardActionType.QUEUE_CATEGORIES_RECEIVED:
			let newStateForCats = {
				...state
			};

			let queueDataForCats: QueueData | undefined = newStateForCats.queueAndDataMap?.get(action.queueName);
			if (queueDataForCats) {
				queueDataForCats.categories = action.categories;
			}

			return newStateForCats;

		case DashboardActionType.QUEUE_CATEGORY_COUNT_RECEIVED:
			let newStateForCatCounts = {
				...state
			};

			let queueDataForCatCounts: QueueData | undefined = newStateForCatCounts.queueAndDataMap?.get(action.queueName);
			if (queueDataForCatCounts) {
				let catAndCountsMap: Map<string, Map<SyncOperation, number>> | undefined = queueDataForCatCounts.categoryAndCounts;
				if (!catAndCountsMap) {
					catAndCountsMap = new Map<string, Map<SyncOperation, number>>();
					queueDataForCatCounts.categoryAndCounts = catAndCountsMap;
				}

				let opAndCountMap: Map<SyncOperation, number> | undefined = catAndCountsMap.get(action.category);
				if (!opAndCountMap) {
					opAndCountMap = new Map<SyncOperation, number>();
					catAndCountsMap.set(action.category, opAndCountMap);
				}

				opAndCountMap.set(action.operation, action.count);
			}

			return newStateForCatCounts;

		default:
			return state;
	}

}
