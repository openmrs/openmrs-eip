import {Action} from "@ngrx/store";
import {Dashboard} from "../dashboard";
import {HttpErrorResponse} from "@angular/common/http";

export enum DashboardActionType {

	LOAD_DASHBOARD = 'LOAD_DASHBOARD',

	DASHBOARD_LOADED = 'DASHBOARD_LOADED',

	LOAD_DASHBOARD_ERROR = 'LOAD_DASHBOARD_ERROR',

	FETCH_COUNT = 'FETCH_COUNT',

	COUNT_RECEIVED = 'COUNT_RECEIVED'

}

export class LoadDashboard implements Action {
	readonly type = DashboardActionType.LOAD_DASHBOARD;
}

export class DashboardLoaded implements Action {

	readonly type = DashboardActionType.DASHBOARD_LOADED;

	constructor(public dashboard: Dashboard) {
	}

}

export class LoadDashboardError implements Action {

	readonly type = DashboardActionType.LOAD_DASHBOARD_ERROR;

	constructor(public error: HttpErrorResponse) {
	}
}

export class FetchCount implements Action {

	readonly type = DashboardActionType.FETCH_COUNT;

	constructor(public entityType: string, public queueName: string) {
	}

}

export class CountReceived implements Action {

	readonly type = DashboardActionType.COUNT_RECEIVED;

	constructor(public count: number, public queueName: string) {
	}

}

export type DashboardAction = LoadDashboard | DashboardLoaded | LoadDashboardError | FetchCount | CountReceived;
