import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {DashboardService} from "../dashboard.service";
import {catchError, map, mergeMap, switchMap} from "rxjs/operators";
import {
	DashboardActionType,
	DashboardLoaded,
	LoadDashboardError,
	QueueCategoriesReceived,
	QueueCountReceived
} from "./dashboard.actions";
import {of} from "rxjs";

@Injectable()
export class DashboardEffects {

	constructor(private actions$: Actions, private dashboardService: DashboardService) {
	}

	loadDashboard$ = createEffect(() =>
		this.actions$.pipe(
			ofType(DashboardActionType.LOAD_DASHBOARD),
			switchMap(() => this.dashboardService.getDashboard()
				.pipe(
					map(dashboard => new DashboardLoaded(dashboard)),
					catchError(err => of(new LoadDashboardError(err)))
				)
			)
		)
	);

	fetchQueueCount$ = createEffect(() =>
		this.actions$.pipe(
			ofType(DashboardActionType.FETCH_QUEUE_COUNT),
			mergeMap(action => this.dashboardService.getCount(action['entityType'], null, null)
				.pipe(
					map(count => new QueueCountReceived(count, action['queueName'])),
					catchError(err => of(new LoadDashboardError(err)))
				)
			)
		)
	);

	fetchQueueCategories$ = createEffect(() =>
		this.actions$.pipe(
			ofType(DashboardActionType.FETCH_QUEUE_CATEGORIES),
			mergeMap(action => this.dashboardService.getCategories(action['entityType'])
				.pipe(
					map(categories => new QueueCategoriesReceived(categories, action['queueName'])),
					catchError(err => of(new LoadDashboardError(err)))
				)
			)
		)
	);

}
