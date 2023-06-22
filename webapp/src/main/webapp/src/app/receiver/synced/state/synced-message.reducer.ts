import {createFeatureSelector, createSelector} from "@ngrx/store";
import {ReceiverSyncedMessage} from "../receiver-synced-message";
import {SyncedMessageAction, SyncedMessageActionType} from "./synced-message.actions";
import {ViewInfo} from "../../shared/view-info";

export interface SyncedMessageState {
	totalCount?: number;

	viewInfo?: ViewInfo;

	syncedItems?: ReceiverSyncedMessage[];

	msgToView?: ReceiverSyncedMessage;

	siteCountMap?: Map<string, number>;
}

const GET_MSG_FEATURE_STATE = createFeatureSelector<SyncedMessageState>('syncedMsgQueue');

export const GET_SYNCED_MSGS = createSelector(
	GET_MSG_FEATURE_STATE,
	state => state.syncedItems
);

export const MSG_TO_VIEW = createSelector(
	GET_MSG_FEATURE_STATE,
	state => state.msgToView
);

export const GET_SYNCED_MSG_TOTAL_COUNT = createSelector(
	GET_MSG_FEATURE_STATE,
	state => state.totalCount
);

export const GET_SYNCED_MSG_VIEW = createSelector(
	GET_MSG_FEATURE_STATE,
	state => state.viewInfo
);

export const GET_SYNCED_MSG_GRP_PROP_COUNT_MAP = createSelector(
	GET_MSG_FEATURE_STATE,
	state => state.siteCountMap
);

export function syncedMessageReducer(state = {}, action: SyncedMessageAction) {

	switch (action.type) {

		case SyncedMessageActionType.SYNCED_MSGS_LOADED:
			return {
				...state,
				totalCount: action.countAndItems?.count,
				syncedItems: action.countAndItems?.items
			};

		case SyncedMessageActionType.VIEW_SYNCED_MSG:
			return {
				...state,
				msgToView: action.message
			};

		case SyncedMessageActionType.CHANGE_SYNCED_MSG_VIEW:
			return {
				...state,
				totalCount: undefined,
				viewInfo: action.viewInfo
			};

		case SyncedMessageActionType.GROUPED_SYNCED_MSGS_LOADED:
			return {
				...state,
				totalCount: action.countAndGroupedItems?.count,
				siteCountMap: action.countAndGroupedItems?.items
			};

		default:
			return state;
	}

}
