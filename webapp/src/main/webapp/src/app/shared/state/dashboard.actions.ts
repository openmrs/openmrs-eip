import {Action} from "@ngrx/store";
import {Dashboard} from "../dashboard";
import {HttpErrorResponse} from "@angular/common/http";

export enum DashboardActionType {

	LOAD_DASHBOARD = 'LOAD_DASHBOARD',

	DASHBOARD_LOADED = 'DASHBOARD_LOADED',

	LOAD_DASHBOARD_ERROR = 'LOAD_DASHBOARD_ERROR'

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

export type DashboardAction = LoadDashboard | DashboardLoaded | LoadDashboardError;
