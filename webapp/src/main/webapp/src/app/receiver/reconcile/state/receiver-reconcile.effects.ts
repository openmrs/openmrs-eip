import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {map, switchMap} from "rxjs/operators";
import {ReconcileService} from "../reconcile.service";
import {ReceiverReconcileActionType, ReceiverReconciliationLoaded} from "./receiver-reconcile.actions";

@Injectable()
export class ReceiverReconcileEffects {

	constructor(private actions$: Actions, private service: ReconcileService) {
	}

	getReconciliation = createEffect(() =>
		this.actions$.pipe(
			ofType(ReceiverReconcileActionType.LOAD_RECONCILIATION),
			switchMap(() => this.service.getReconciliation()
				.pipe(
					map(reconciliation => new ReceiverReconciliationLoaded(reconciliation))
				)
			)
		)
	);

}
