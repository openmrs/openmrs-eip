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
		['synced', new QueueData()],
		['error', new QueueData()],
		['conflict', new QueueData()],
		['event', new QueueData()],
		['sender-sync', new QueueData()],
		['sender-error', new QueueData()]
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

export const GET_SYNCED_CATEGORIES = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('synced')?.categories
);

export const GET_SYNCED_CATEGORY_COUNTS = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('synced')?.categoryAndCounts
);

export const GET_ERROR_COUNT = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('error')?.count
);

export const GET_ERROR_CATEGORIES = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('error')?.categories
);

export const GET_ERROR_CATEGORY_COUNTS = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('error')?.categoryAndCounts
);

export const GET_CONFLICT_COUNT = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('conflict')?.count
);

export const GET_CONFLICT_CATEGORIES = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('conflict')?.categories
);

export const GET_CONFLICT_CATEGORY_COUNTS = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('conflict')?.categoryAndCounts
);

export const GET_EVENT_COUNT = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('event')?.count
);

export const GET_EVENT_CATEGORIES = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('event')?.categories
);

export const GET_EVENT_CATEGORY_COUNTS = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('event')?.categoryAndCounts
);

export const GET_SENDER_SYNC_COUNT = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('sender-sync')?.count
);

export const GET_SENDER_SYNC_CATEGORIES = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('sender-sync')?.categories
);

export const GET_SENDER_SYNC_CATEGORY_COUNTS = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('sender-sync')?.categoryAndCounts
);

export const GET_SENDER_ERROR_COUNT = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('sender-error')?.count
);

export const GET_SENDER_ERROR_CATEGORIES = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('sender-error')?.categories
);

export const GET_SENDER_ERROR_CATEGORY_COUNTS = createSelector(
	GET_DASHBOARD_FEATURE_STATE,
	state => state.queueAndDataMap?.get('sender-error')?.categoryAndCounts
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

				//TODO Add action to remove stale categories without updating UI to avoid flicker

				//Removes any stale categories from then ngrx state from previous load operations that are no longer
				//contained in the current list of categories otherwise the effective total of category counts will be
				//out of sync with the queue total
				let catAndCountsMapForCats: Map<string, Map<SyncOperation, number>> | undefined = queueDataForCats.categoryAndCounts;
				if (catAndCountsMapForCats) {
					for (let category of catAndCountsMapForCats?.keys()) {
						if (action.categories.indexOf(category) < 0) {
							catAndCountsMapForCats.delete(category);
						}
					}
				}
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
				} else {
					catAndCountsMap = new Map<string, Map<SyncOperation, number>>(catAndCountsMap);
				}

				queueDataForCatCounts.categoryAndCounts = catAndCountsMap;

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
