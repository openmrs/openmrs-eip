import {Action} from "@ngrx/store";
import {ErrorDetails} from "../error-details";

export enum SenderDashboardActionType {

	FETCH_COUNT_BY_STATUS = 'FETCH_COUNT_BY_STATUS',

	COUNT_BY_STATUS_RECEIVED = 'COUNT_BY_STATUS_RECEIVED',

	FETCH_ERROR_DETAILS = 'FETCH_ERROR_DETAILS',

	ERROR_DETAILS_RECEIVED = 'ERROR_DETAILS_RECEIVED'

}

export class FetchCountByStatus implements Action {
	readonly type = SenderDashboardActionType.FETCH_COUNT_BY_STATUS;
}

export class CountByStatusReceived implements Action {

	readonly type = SenderDashboardActionType.COUNT_BY_STATUS_RECEIVED;

	constructor(public countByStatus: Map<string, number>) {
	}

}

export class FetchErrorDetails implements Action {
	readonly type = SenderDashboardActionType.FETCH_ERROR_DETAILS;
}

export class ErrorDetailsReceived implements Action {

	readonly type = SenderDashboardActionType.ERROR_DETAILS_RECEIVED;

	constructor(public errorDetails: ErrorDetails) {
	}

}

export type SenderDashboardAction =
	FetchCountByStatus
	| CountByStatusReceived
	| FetchErrorDetails
	| ErrorDetailsReceived;
