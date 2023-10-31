import {Action} from "@ngrx/store";

export enum SenderDashboardActionType {

	FETCH_COUNT_BY_STATUS = 'FETCH_COUNT_BY_STATUS',

	COUNT_BY_STATUS_RECEIVED = 'COUNT_BY_STATUS_RECEIVED'

}

export class FetchCountByStatus implements Action {
	readonly type = SenderDashboardActionType.FETCH_COUNT_BY_STATUS;
}

export class CountByStatusReceived implements Action {

	readonly type = SenderDashboardActionType.COUNT_BY_STATUS_RECEIVED;

	constructor(public countByStatus: Map<string, number>) {
	}

}

export type SenderDashboardAction = FetchCountByStatus | CountByStatusReceived;
