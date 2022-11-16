import {createFeatureSelector, createSelector} from "@ngrx/store";
import {ReceiverArchiveAction, ReceiverArchiveActionType} from "./receiver-archive.actions";
import {ViewInfo} from "../../shared/view-info";
import {ReceiverSyncArchive} from "../receiver-sync-archive";

export interface ReceiverArchiveState {

	totalCount?: number;

	viewInfo?: ViewInfo;

	syncItems?: ReceiverSyncArchive[];

	siteCountMap?: Map<string, number>;

}

const GET_SYNC_ARCHIVE_FEATURE_STATE = createFeatureSelector<ReceiverArchiveState>('receiverArchiveQueue');

export const GET_SYNC_ARCHIVE = createSelector(
	GET_SYNC_ARCHIVE_FEATURE_STATE,
	state => state.syncItems
);

export const GET_TOTAL_COUNT = createSelector(
	GET_SYNC_ARCHIVE_FEATURE_STATE,
	state => state.totalCount
);

export const GET_VIEW = createSelector(
	GET_SYNC_ARCHIVE_FEATURE_STATE,
	state => state.viewInfo
);

export const GET_GRP_PROP_COUNT_MAP = createSelector(
	GET_SYNC_ARCHIVE_FEATURE_STATE,
	state => state.siteCountMap
);

export function syncArchiveReducer(state = {}, action: ReceiverArchiveAction) {

	switch (action.type) {

		case ReceiverArchiveActionType.SYNC_ARCHIVES_LOADED:
			return {
				...state,
				totalCount: action.countAndItems?.count,
				syncItems: action.countAndItems?.items
			};

		case ReceiverArchiveActionType.CHANGE_ARCHIVE_VIEW:
			return {
				...state,
				totalCount: undefined,
				viewInfo: action.viewInfo
			};

		case ReceiverArchiveActionType.GROUPED_ARCHIVES_LOADED:
			return {
				...state,
				totalCount: action.countAndGroupedItems?.count,
				siteCountMap: action.countAndGroupedItems?.items
			};

		default:
			return state;
	}

}
