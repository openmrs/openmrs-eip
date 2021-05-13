import {Action} from "@ngrx/store";
import {SenderError} from "../sender-error";

export enum SenderErrorActionType {
	SENDER_ERRORS_LOADED = 'SENDER_ERRORS_LOADED',
	VIEW_SENDER_ERROR = 'VIEW_SENDER_ERROR'
}

export class SenderErrorsLoaded implements Action {

	readonly type = SenderErrorActionType.SENDER_ERRORS_LOADED;

	constructor(public errors?: SenderError[]) {
	}

}

export class ViewSenderError implements Action {

	readonly type = SenderErrorActionType.VIEW_SENDER_ERROR;

	constructor(public error?: SenderError) {
	}

}

export type SenderErrorAction = SenderErrorsLoaded | ViewSenderError;
