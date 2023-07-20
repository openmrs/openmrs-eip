import {ConflictAction, ConflictActionType} from "./conflict.actions";
import {createFeatureSelector, createSelector} from "@ngrx/store";
import {Conflict} from "../conflict";
import {ConflictCountAndItems} from "../confict-count-and-items";
import {VerifyTaskStatus} from "../verify-task-status";

export interface ConflictState {
	countAndItems: ConflictCountAndItems;
	conflictToView?: Conflict;
	verifyTaskStatus?: VerifyTaskStatus;
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

export const GET_VERIFY_TASK_STATUS = createSelector(
	GET_CONFLICTS_FEATURE_STATE,
	state => state.verifyTaskStatus
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

		case ConflictActionType.VERIFY_TASK_STATUS_UPDATED:
			return {
				...state,
				verifyTaskStatus: action.verifyTaskStatus,

			};

		default:
			return state;
	}

}
