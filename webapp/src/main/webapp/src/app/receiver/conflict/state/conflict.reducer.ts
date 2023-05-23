import {ConflictAction, ConflictActionType} from "./conflict.actions";
import {createFeatureSelector, createSelector} from "@ngrx/store";
import {Conflict} from "../conflict";
import {ConflictCountAndItems} from "../confict-count-and-items";

export interface ConflictState {
	countAndItems: ConflictCountAndItems;
	conflictToView?: Conflict;
	falseConflicts?: number;
	cleanedConflicts?: number;
}

const GET_CONFLICTS_FEATURE_STATE = createFeatureSelector<ConflictState>('conflictQueue');

export const GET_CONFLICTS = createSelector(
	GET_CONFLICTS_FEATURE_STATE,
	state => state.countAndItems
);

export const CONFLICT_TO_VIEW = createSelector(
	GET_CONFLICTS_FEATURE_STATE,
	state => state.conflictToView
);

export const GET_FALSE_CONFLICTS = createSelector(
	GET_CONFLICTS_FEATURE_STATE,
	state => state.falseConflicts
);

export const GET_CLEANED_CONFLICTS = createSelector(
	GET_CONFLICTS_FEATURE_STATE,
	state => state.cleanedConflicts
);

const initialState: ConflictState = {
	countAndItems: new ConflictCountAndItems()
};

export function conflictReducer(state = initialState, action: ConflictAction) {

	switch (action.type) {

		case ConflictActionType.CONFLICTS_LOADED:
			return {
				...state,
				countAndItems: action.countAndItems
			};

		case ConflictActionType.VIEW_CONFLICT:
			return {
				...state,
				conflictToView: action.conflictToView
			};

		case ConflictActionType.CONFLICTS_VERIFIED:
			return {
				...state,
				falseConflicts: action.falseConflicts
			};

		case ConflictActionType.CONFLICTS_CLEANED:
			return {
				...state,
				cleanedConflicts: action.cleanedConflicts
			};

		default:
			return state;
	}

}
