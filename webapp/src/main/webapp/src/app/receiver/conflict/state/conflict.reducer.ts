import {ConflictAction, ConflictActionType} from "./conflict.actions";
import {createFeatureSelector, createSelector} from "@ngrx/store";
import {ConflictCountAndItems} from "../confict-count-and-items";
import {VerifyTaskStatus} from "../verify-task-status";
import {Diff} from "../diff";

export interface ConflictState {
	countAndItems: ConflictCountAndItems;
	diff?: Diff;
	verifyTaskStatus?: VerifyTaskStatus;
}

const GET_CONFLICTS_FEATURE_STATE = createFeatureSelector<ConflictState>('conflictQueue');

export const GET_CONFLICTS = createSelector(
	GET_CONFLICTS_FEATURE_STATE,
	state => state.countAndItems
);

export const GET_DIFF = createSelector(
	GET_CONFLICTS_FEATURE_STATE,
	state => state.diff
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

		case ConflictActionType.VIEW_DIFF:
			return {
				...state,
				diff: action.diff
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
