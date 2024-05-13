import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {map, switchMap} from "rxjs/operators";
import {
	SenderHistoryLoaded,
	SenderReconcileActionType,
	SenderReconciliationLoaded,
	SenderTableReconciliationsLoaded
} from "./sender-reconcile.actions";
import {SenderReconcileService} from "../sender-reconcile.service";

@Injectable()
export class SenderReconcileEffects {

	constructor(private actions$: Actions, private service: SenderReconcileService) {
	}

	getReconciliation = createEffect(() =>
		this.actions$.pipe(
			ofType(SenderReconcileActionType.LOAD_RECONCILIATION),
			switchMap(() => this.service.getReconciliation()
				.pipe(
					map(reconciliation => new SenderReconciliationLoaded(reconciliation))
				)
			)
		)
	);

	getTableReconciliations = createEffect(() =>
		this.actions$.pipe(
			ofType(SenderReconcileActionType.LOAD_TABLE_RECONCILIATIONS),
			switchMap(() => this.service.getIncompleteTableReconciliations()
				.pipe(
					map(tableRecs => new SenderTableReconciliationsLoaded(tableRecs))
				)
			)
		)
	);

	getHistory = createEffect(() =>
		this.actions$.pipe(
			ofType(SenderReconcileActionType.LOAD_HISTORY),
			switchMap(() => this.service.getHistory()
				.pipe(
					map(recHistory => new SenderHistoryLoaded(recHistory))
				)
			)
		)
	);

}
