import {createFeatureSelector, createSelector} from "@ngrx/store";
import {ReceiverError} from "../receiver-error";
import {ReceiverErrorAction, ReceiverErrorActionType} from "./error.actions";

export interface ReceiverErrorState {
	errors: ReceiverError[];
	errorToView?: ReceiverError;
}

const GET_RECEIVER_ERRORS_FEATURE_STATE = createFeatureSelector<ReceiverErrorState>('receiverErrorQueue');

export const GET_RECEIVER_ERRORS = createSelector(
	GET_RECEIVER_ERRORS_FEATURE_STATE,
	state => state.errors
);

export const RECEIVER_ERROR_TO_VIEW = createSelector(
	GET_RECEIVER_ERRORS_FEATURE_STATE,
	state => state.errorToView
);

const initialReceiverState: ReceiverErrorState = {
	errors: []
};

export function receiverErrorReducer(state = initialReceiverState, action: ReceiverErrorAction) {

	switch (action.type) {

		case ReceiverErrorActionType.RECEIVER_ERRORS_LOADED:
			return {
				...state,
				errors: action.errors
			};

		case ReceiverErrorActionType.VIEW_RECEIVER_ERROR:
			return {
				...state,
				errorToView: action.error
			};

		default:
			return state;
	}

}
