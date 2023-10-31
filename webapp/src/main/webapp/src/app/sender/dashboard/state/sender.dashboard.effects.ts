import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {catchError, map, switchMap} from "rxjs/operators";
import {of} from "rxjs";
import {CountByStatusReceived, SenderDashboardActionType} from "./sender.dashboard.actions";
import {SenderDashboardService} from "../sender.dashboard.service";
import {LoadDashboardError} from "../../../shared/dashboard/state/dashboard.actions";

@Injectable()
export class SenderDashboardEffects {

	constructor(private actions$: Actions, private service: SenderDashboardService) {
	}

	getSyncCountByStatus$ = createEffect(() =>
		this.actions$.pipe(
			ofType(SenderDashboardActionType.FETCH_COUNT_BY_STATUS),
			switchMap(() => this.service.getSenderSyncCountByStatus()
				.pipe(
					map(countByStatus => new CountByStatusReceived(countByStatus)),
					catchError(err => of(new LoadDashboardError(err)))
				)
			)
		)
	);

}
