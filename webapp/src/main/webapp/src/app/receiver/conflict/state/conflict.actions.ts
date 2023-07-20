import {Conflict} from "../conflict";
import {Action} from "@ngrx/store";
import {ConflictCountAndItems} from "../confict-count-and-items";
import {VerifyTaskStatus} from "../verify-task-status";

export enum ConflictActionType {
	CONFLICTS_LOADED = 'CONFLICTS_LOADED',
	VIEW_CONFLICT = 'VIEW_CONFLICT',
	VERIFY_TASK_STATUS_UPDATED = 'VERIFY_TASK_STATUS_UPDATED'
}

export class ConflictsLoaded implements Action {

	readonly type = ConflictActionType.CONFLICTS_LOADED;

	constructor(public countAndItems?: ConflictCountAndItems) {
	}

}

export class ViewConflict implements Action {

	readonly type = ConflictActionType.VIEW_CONFLICT;

	constructor(public conflictToView?: Conflict) {
	}

}

export class VerifyTaskStatusUpdated implements Action {

	readonly type = ConflictActionType.VERIFY_TASK_STATUS_UPDATED;

	constructor(public verifyTaskStatus?: VerifyTaskStatus) {
	}

}

export type ConflictAction = ConflictsLoaded | ViewConflict | VerifyTaskStatusUpdated;
