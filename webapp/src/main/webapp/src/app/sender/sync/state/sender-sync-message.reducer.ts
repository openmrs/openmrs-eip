import {createFeatureSelector, createSelector} from "@ngrx/store";
import {SenderSyncMessageAction, SenderSyncMessageActionType} from "./sender-sync-message.actions";
import {SenderSyncMessageCountAndItems} from "../sender-sync-message-count-and-items";

export interface SenderSyncMessageState {
	countAndItems: SenderSyncMessageCountAndItems;
}

const GET_DB_MSGS_FEATURE_STATE = createFeatureSelector<SenderSyncMessageState>('syncQueue');

export const GET_MSGS = createSelector(
	GET_DB_MSGS_FEATURE_STATE,
	state => state.countAndItems
);

const initialState: SenderSyncMessageState = {
	countAndItems: new SenderSyncMessageCountAndItems()
};

export function senderSyncMessageReducer(state = initialState, action: SenderSyncMessageAction) {

	switch (action.type) {

		case SenderSyncMessageActionType.MSGS_LOADED:
			return {
				...state,
				countAndItems: action.countAndItems
			};

		default:
			return state;
	}

}
