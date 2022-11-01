import {createFeatureSelector, createSelector} from "@ngrx/store";
import {ReceiverSyncArchiveCountAndItems} from "../receiver-sync-archive-count-and-items";
import {ReceiverArchiveAction, ReceiverArchiveActionType} from "./receiver-archive.actions";

export interface ReceiverArchiveState {
	countAndItems: ReceiverSyncArchiveCountAndItems;
}

const GET_SYNC_ARCHIVE_FEATURE_STATE = createFeatureSelector<ReceiverArchiveState>('syncArquiveQueue');

export const GET_SYNC_ARCHIVE = createSelector(
	GET_SYNC_ARCHIVE_FEATURE_STATE,
	state => state.countAndItems
);

const initialState: ReceiverArchiveState = {
	countAndItems: new ReceiverSyncArchiveCountAndItems()
};

export function syncArchiveReducer(state = initialState, action: ReceiverArchiveAction) {

	switch (action.type) {

		case ReceiverArchiveActionType.SYNC_ARCHIVE_LOADED:
			return {
				...state,
				countAndItems: action.countAndItems
			};

		default:
			return state;
	}

}
