import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {map, switchMap} from "rxjs/operators";
import {
	ReceiverReconcileActionType,
	ReceiverReconcileProgressLoaded,
	ReceiverReconciliationLoaded
} from "./receiver-reconcile.actions";
import {ReceiverReconcileService} from "../receiver-reconcile.service";

@Injectable()
export class ReceiverReconcileEffects {

	constructor(private actions$: Actions, private service: ReceiverReconcileService) {
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

	startReconciliation = createEffect(() =>
		this.actions$.pipe(
			ofType(ReceiverReconcileActionType.START_RECONCILIATION),
			switchMap(() => this.service.startReconciliation()
				.pipe(
					map(reconciliation => new ReceiverReconciliationLoaded(reconciliation))
				)
			)
		)
	);

	getReconciliationProgress = createEffect(() =>
		this.actions$.pipe(
			ofType(ReceiverReconcileActionType.LOAD_PROGRESS),
			switchMap(() => this.service.getReconciliationProgress()
				.pipe(
					map(progress => new ReceiverReconcileProgressLoaded(progress))
				)
			)
		)
	);

}
