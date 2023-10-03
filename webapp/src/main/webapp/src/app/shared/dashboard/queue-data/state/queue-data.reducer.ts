import {createFeatureSelector, createSelector} from "@ngrx/store";
import {HttpErrorResponse} from "@angular/common/http";
import {QueueDataAction, QueueDataActionType} from "./queue-data.actions";

export interface QueueDataState {
	count: number;
	error: HttpErrorResponse;
}

const GET_DATA_FEATURE_STATE = createFeatureSelector<QueueDataState>('syncQueueData');

export const GET_COUNT = createSelector(
	GET_DATA_FEATURE_STATE,
	state => state.count
);

export function queueDataReducer(state = {}, action: QueueDataAction) {

	switch (action.type) {

		case QueueDataActionType.LOADED:
			return {
				...state,
				queueData: action.queueData
			};

		case QueueDataActionType.COUNT_RECEIVED:
			return {
				...state,
				count: action.count
			};

		case QueueDataActionType.LOAD_ERROR:
			return {
				...state,
				error: action.error
			};

		default:
			return state;
	}

}
