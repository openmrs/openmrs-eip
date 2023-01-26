import {createFeatureSelector, createSelector} from "@ngrx/store";
import {AppAction, AppActionType} from "./app.actions";
import {AppProperties} from "../receiver/shared/app-properties";

export interface AppState {
	properties: AppProperties;
}

const GET_PROPS_FEATURE_STATE = createFeatureSelector<AppState>('props');


export const GET_PROPS = createSelector(
	GET_PROPS_FEATURE_STATE,
	state => state.properties
);

const initialState: AppState = {
	properties: new AppProperties()
};

export function appReducer(state = initialState, action: AppAction) {

	switch (action.type) {

		case AppActionType.PROPERTIES_LOADED:
			return {
				...state,
				properties: action.properties
			};

		default:
			return state;
	}

}
