import {createFeatureSelector, createSelector} from "@ngrx/store";
import {ReceiverError} from "../receiver-error";
import {ReceiverErrorAction, ReceiverErrorActionType} from "./error.actions";
import {ReceiverErrorCountAndItems} from "../receiver-error-count-and-items";

export interface ReceiverErrorState {
	countAndItems: ReceiverErrorCountAndItems;
	errorToView?: ReceiverError;
}

const GET_RECEIVER_ERRORS_FEATURE_STATE = createFeatureSelector<ReceiverErrorState>('receiverErrorQueue');

export const GET_RECEIVER_ERRORS = createSelector(
	GET_RECEIVER_ERRORS_FEATURE_STATE,
	state => state.countAndItems
);

export const RECEIVER_ERROR_TO_VIEW = createSelector(
	GET_RECEIVER_ERRORS_FEATURE_STATE,
	state => state.errorToView
);

const initialReceiverState: ReceiverErrorState = {
	countAndItems: new ReceiverErrorCountAndItems()
};

export function receiverErrorReducer(state = initialReceiverState, action: ReceiverErrorAction) {

	switch (action.type) {

		case ReceiverErrorActionType.RECEIVER_ERRORS_LOADED:
			return {
				...state,
				countAndItems: action.countAndItems
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
