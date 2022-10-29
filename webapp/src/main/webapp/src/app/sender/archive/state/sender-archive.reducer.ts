import { createFeatureSelector, createSelector } from "@ngrx/store";
import { SenderSyncArchiveCountAndItems } from "../sender-sync-archive-count-and-items";
import { SenderArchiveAction, SenderArchiveActionType } from "./sender-archive.actions";

export interface SenderArchiveState {
	countAndItems: SenderSyncArchiveCountAndItems;
}

const GET_SYNC_ARCHIVE_FEATURE_STATE = createFeatureSelector<SenderArchiveState>('senderArchiveQueue');

export const GET_SYNC_ARCHIVE = createSelector(
	GET_SYNC_ARCHIVE_FEATURE_STATE,
	state => state.countAndItems
);

const initialState: SenderArchiveState = {
	countAndItems: new SenderSyncArchiveCountAndItems()
};

export function senderArchiveReducer(state = initialState, action: SenderArchiveAction) {

	switch (action.type) {

		case SenderArchiveActionType.SENDER_ARCHIVED_LOADED:
			return {
				...state,
				countAndItems: action.countAndItems
			};
		default:
			return state;
	}

}
