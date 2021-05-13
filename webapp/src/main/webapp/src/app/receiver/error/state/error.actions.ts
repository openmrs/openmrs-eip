import {Action} from "@ngrx/store";
import {ReceiverError} from "../receiver-error";

export enum ReceiverErrorActionType {
	RECEIVER_ERRORS_LOADED = 'RECEIVER_ERRORS_LOADED',
	VIEW_RECEIVER_ERROR = 'VIEW_RECEIVER_ERROR'
}

export class ReceiverErrorsLoaded implements Action {

	readonly type = ReceiverErrorActionType.RECEIVER_ERRORS_LOADED;

	constructor(public errors?: ReceiverError[]) {
	}

}

export class ViewReceiverError implements Action {

	readonly type = ReceiverErrorActionType.VIEW_RECEIVER_ERROR;

	constructor(public error?: ReceiverError) {
	}

}

export type ReceiverErrorAction = ReceiverErrorsLoaded | ViewReceiverError;
