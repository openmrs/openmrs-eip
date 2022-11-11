import {createFeatureSelector, createSelector} from "@ngrx/store";
import {ReceiverSyncMessageCountAndItems} from "../receiver-sync-message-count-and-items";
import {ReceiverSyncMessage} from "../receiver-sync-message";
import {SyncMessageAction, SyncMessageActionType} from "./sync-message.actions";
import {ViewInfo} from "../../shared/view-info";

export interface SyncMessageState {
	totalCount?: number;
	viewInfo?: ViewInfo;
	syncItems?: ReceiverSyncMessage[];
	msgToView?: ReceiverSyncMessage;
}

const GET_MSG_FEATURE_STATE = createFeatureSelector<SyncMessageState>('syncMsgQueue');

export const GET_MSGS = createSelector(
	GET_MSG_FEATURE_STATE,
	state => state.syncItems
);

export const MSG_TO_VIEW = createSelector(
	GET_MSG_FEATURE_STATE,
	state => state.msgToView
);

export const GET_TOTAL_COUNT = createSelector(
	GET_MSG_FEATURE_STATE,
	state => state.totalCount
);

export const GET_VIEW = createSelector(
	GET_MSG_FEATURE_STATE,
	state => state.viewInfo
);

export function syncMessageReducer(state = {}, action: SyncMessageAction) {

	switch (action.type) {

		case SyncMessageActionType.MSGS_LOADED:
			return {
				...state,
				totalCount: action.countAndItems?.count,
				syncItems: action.countAndItems?.items
			};

		case SyncMessageActionType.VIEW_MSG:
			return {
				...state,
				msgToView: action.message
			};

		case SyncMessageActionType.CHANGE_VIEW:
			return {
				...state,
				totalCount: undefined,
				viewInfo: action.viewInfo
			};

		default:
			return state;
	}

}
