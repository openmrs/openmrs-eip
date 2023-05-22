import {Conflict} from "../conflict";
import {Action} from "@ngrx/store";
import {ConflictCountAndItems} from "../confict-count-and-items";

export enum ConflictActionType {
	CONFLICTS_LOADED = 'CONFLICTS_LOADED',
	VIEW_CONFLICT = 'VIEW_CONFLICT',
	CONFLICTS_VERIFIED = 'CONFLICTS_VERIFIED'
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

export class ConflictsVerified implements Action {

	readonly type = ConflictActionType.CONFLICTS_VERIFIED;

	constructor(public falseConflicts?: number) {
	}

}

export type ConflictAction = ConflictsLoaded | ViewConflict | ConflictsVerified;
