import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {DashboardService} from "../dashboard.service";
import {catchError, map, mergeMap} from "rxjs/operators";
import {
	DashboardActionType,
	LoadDashboardError,
	QueueCategoriesReceived,
	QueueCategoryCountReceived,
	QueueCountReceived
} from "./dashboard.actions";
import {of} from "rxjs";

@Injectable()
export class DashboardEffects {

	readonly QUEUE_AND_TYPE_MAP = new Map<string, string>([
		['sync', 'SyncMessage'],
		['synced', 'SyncedMessage'],
		['error', 'ReceiverRetryQueueItem'],
		['conflict', 'ConflictQueueItem'],
		['event', 'DebeziumEvent'],
		['sender-sync', 'SenderSyncMessage'],
		['sender-error', 'SenderRetryQueueItem']
	]);

	constructor(private actions$: Actions, private dashboardService: DashboardService) {
	}

	fetchQueueCount$ = createEffect(() =>
		this.actions$.pipe(
			ofType(DashboardActionType.FETCH_QUEUE_COUNT),
			mergeMap(action => this.dashboardService.getCount(this.QUEUE_AND_TYPE_MAP.get(action['queueName']))
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
			mergeMap(action => this.dashboardService.getCategories(this.QUEUE_AND_TYPE_MAP.get(action['queueName']))
				.pipe(
					map(categories => new QueueCategoriesReceived(categories, action['queueName'])),
					catchError(err => of(new LoadDashboardError(err)))
				)
			)
		)
	);

	fetchQueueCategoryCount$ = createEffect(() =>
		this.actions$.pipe(
			ofType(DashboardActionType.FETCH_QUEUE_CATEGORY_COUNT),
			mergeMap(action => this.dashboardService.getCount(this.QUEUE_AND_TYPE_MAP.get(action['queueName']), action['category'], action['operation'])
				.pipe(
					map(count => new QueueCategoryCountReceived(count, action['queueName'], action['category'], action['operation'])),
					catchError(err => of(new LoadDashboardError(err)))
				)
			)
		)
	);

}
