import {createFeatureSelector, createSelector} from "@ngrx/store";
import {DbEventCountAndItems} from "../db-event-count-and-items";
import {DbEventAction, DbEventActionType} from "./db-event.actions";

export interface DbEventState {
	countAndItems: DbEventCountAndItems;
}

const GET_DB_EVENT_FEATURE_STATE = createFeatureSelector<DbEventState>('eventQueue');

export const GET_EVENTS = createSelector(
	GET_DB_EVENT_FEATURE_STATE,
	state => state.countAndItems
);

const initialState: DbEventState = {
	countAndItems: new DbEventCountAndItems()
};

export function dbEventReducer(state = initialState, action: DbEventAction) {

	switch (action.type) {

		case DbEventActionType.EVENTS_LOADED:
			return {
				...state,
				countAndItems: action.countAndItems
			};

		default:
			return state;
	}

}
