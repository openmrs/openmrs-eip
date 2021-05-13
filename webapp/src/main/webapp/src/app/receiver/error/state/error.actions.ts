import {Action} from "@ngrx/store";
import {ReceiverError} from "../receiver-error";
import {ReceiverErrorCountAndItems} from "../receiver-error-count-and-items";

export enum ReceiverErrorActionType {
	RECEIVER_ERRORS_LOADED = 'RECEIVER_ERRORS_LOADED',
	VIEW_RECEIVER_ERROR = 'VIEW_RECEIVER_ERROR'
}

export class ReceiverErrorsLoaded implements Action {

	readonly type = ReceiverErrorActionType.RECEIVER_ERRORS_LOADED;

	constructor(public countAndItems?: ReceiverErrorCountAndItems) {
	}

}

export class ViewReceiverError implements Action {

	readonly type = ReceiverErrorActionType.VIEW_RECEIVER_ERROR;

	constructor(public error?: ReceiverError) {
	}

}

export type ReceiverErrorAction = ReceiverErrorsLoaded | ViewReceiverError;
