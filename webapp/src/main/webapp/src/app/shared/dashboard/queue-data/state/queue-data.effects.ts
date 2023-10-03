import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {catchError, map, mergeMap} from "rxjs/operators";
import {of} from "rxjs";
import {CountReceived, LoadError, QueueDataActionType} from "./queue-data.actions";
import {DashboardService} from "../../../dashboard.service";

@Injectable()
export class QueueDataEffects {

	constructor(private actions$: Actions, private dashboardService: DashboardService) {
	}

	fetchCount$ = createEffect(() =>
		this.actions$.pipe(
			ofType(QueueDataActionType.FETCH_COUNT),
			mergeMap(action => this.dashboardService.getCount(action['entityType'], null, null)
				.pipe(
					map(count => new CountReceived(count)),
					catchError(err => of(new LoadError(err)))
				)
			)
		)
	);

}
