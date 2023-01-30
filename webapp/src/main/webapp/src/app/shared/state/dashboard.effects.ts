import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {DashboardService} from "../dashboard.service";
import {catchError, map, switchMap} from "rxjs/operators";
import {DashboardActionType, DashboardLoaded, LoadDashboardError} from "./dashboard.actions";
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

}
