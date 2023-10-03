import {Action} from "@ngrx/store";
import {HttpErrorResponse} from "@angular/common/http";
import {QueueData} from "../queue-data";

export enum QueueDataActionType {

	LOAD = 'LOAD',

	FETCH_COUNT = 'FETCH_COUNT',

	COUNT_RECEIVED = 'COUNT_RECEIVED',

	LOADED = 'LOADED',

	LOAD_ERROR = 'LOAD_ERROR'

}

export class Load implements Action {
	readonly type = QueueDataActionType.LOAD;
}

export class FetchCount implements Action {

	readonly type = QueueDataActionType.FETCH_COUNT;

	constructor(public entityType: string) {
	}

}

export class CountReceived implements Action {

	readonly type = QueueDataActionType.COUNT_RECEIVED;

	constructor(public count: number) {
	}

}

export class Loaded implements Action {

	readonly type = QueueDataActionType.LOADED;

	constructor(public queueData: QueueData) {
	}

}

export class LoadError implements Action {

	readonly type = QueueDataActionType.LOAD_ERROR;

	constructor(public error: HttpErrorResponse) {
	}
}

export type QueueDataAction = Load | FetchCount | CountReceived | Loaded | LoadError;
