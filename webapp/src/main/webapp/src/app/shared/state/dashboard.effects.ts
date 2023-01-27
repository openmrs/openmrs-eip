import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {DashboardService} from "../dashboard.service";
import {map, switchMap} from "rxjs/operators";
import {DashboardActionType, DashboardLoaded} from "./dashboard.actions";

@Injectable()
export class DashboardEffects {

	constructor(private actions$: Actions, private dashboardService: DashboardService) {
	}

	loadDashboard$ = createEffect(() =>
		this.actions$.pipe(
			ofType(DashboardActionType.LOAD_DASHBOARD),
			switchMap(() => this.dashboardService.getDashboard()
				.pipe(
					map(dashboard => new DashboardLoaded(dashboard))
				)
			)
		)
	);

}
