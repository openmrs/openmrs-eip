import {createFeatureSelector, createSelector} from "@ngrx/store";
import {SiteStatusCountAndItems} from "../site-status-count-and-items";
import {SiteStatusAction, SiteStatusActionType} from "./site-status.actions";

export interface SiteStatusState {
	countAndItems: SiteStatusCountAndItems;
}

const GET_STATUS_FEATURE_STATE = createFeatureSelector<SiteStatusState>('siteStatuses');

export const GET_STATUSES = createSelector(
	GET_STATUS_FEATURE_STATE,
	state => state.countAndItems
);

const initialState: SiteStatusState = {
	countAndItems: new SiteStatusCountAndItems()
};

export function siteStatusReducer(state = initialState, action: SiteStatusAction) {

	switch (action.type) {

		case SiteStatusActionType.STATUTES_LOADED:
			return {
				...state,
				countAndItems: action.countAndItems
			};

		default:
			return state;
	}

}
