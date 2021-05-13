import {Conflict} from "../conflict";
import {Action} from "@ngrx/store";
import {ConflictCountAndItems} from "../confict-count-and-items";

export enum ConflictActionType {
	CONFLICTS_LOADED = 'CONFLICTS_LOADED',
	VIEW_CONFLICT = 'VIEW_CONFLICT'
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

export type ConflictAction = ConflictsLoaded | ViewConflict;
