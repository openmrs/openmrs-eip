import {Action} from "@ngrx/store";
import {Dashboard} from "../dashboard";
import {HttpErrorResponse} from "@angular/common/http";

export enum DashboardActionType {

	LOAD_DASHBOARD = 'LOAD_DASHBOARD',

	DASHBOARD_LOADED = 'DASHBOARD_LOADED',

	LOAD_DASHBOARD_ERROR = 'LOAD_DASHBOARD_ERROR',

	FETCH_QUEUE_COUNT = 'FETCH_QUEUE_COUNT',

	QUEUE_COUNT_RECEIVED = 'QUEUE_COUNT_RECEIVED',

	FETCH_QUEUE_CATEGORIES = 'FETCH_QUEUE_CATEGORIES',

	QUEUE_CATEGORIES_RECEIVED = 'QUEUE_CATEGORIES_RECEIVED'

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

export class FetchQueueCount implements Action {

	readonly type = DashboardActionType.FETCH_QUEUE_COUNT;

	constructor(public entityType: string, public queueName: string) {
	}

}

export class QueueCountReceived implements Action {

	readonly type = DashboardActionType.QUEUE_COUNT_RECEIVED;

	constructor(public count: number, public queueName: string) {
	}

}

export class FetchQueueCategories implements Action {

	readonly type = DashboardActionType.FETCH_QUEUE_CATEGORIES;

	constructor(public entityType: string, public queueName: string) {
	}

}

export class QueueCategoriesReceived implements Action {

	readonly type = DashboardActionType.QUEUE_CATEGORIES_RECEIVED;

	constructor(public categories: string[], public queueName: string) {
	}

}

export type DashboardAction =
	LoadDashboard
	| DashboardLoaded
	| LoadDashboardError
	| FetchQueueCount
	| QueueCountReceived
	| FetchQueueCategories
	| QueueCategoriesReceived;
