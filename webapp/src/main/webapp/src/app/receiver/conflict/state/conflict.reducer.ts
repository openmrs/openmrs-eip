import {ConflictAction, ConflictActionType} from "./conflict.actions";
import {createFeatureSelector, createSelector} from "@ngrx/store";
import {ConflictCountAndItems} from "../confict-count-and-items";
import {ConflictTaskStatus} from "../conflict-task-status";
import {Diff} from "../diff";

export interface ConflictState {
	countAndItems: ConflictCountAndItems;
	diff?: Diff;
	verifyTaskStatus?: ConflictTaskStatus;
	resolverTaskStatus?: ConflictTaskStatus;
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

export const GET_RESOLVER_TASK_STATUS = createSelector(
	GET_CONFLICTS_FEATURE_STATE,
	state => state.resolverTaskStatus
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

		case ConflictActionType.RESOLVER_TASK_STATUS_UPDATED:
			return {
				...state,
				resolverTaskStatus: action.resolverTaskStatus,

			};

		default:
			return state;
	}

}
