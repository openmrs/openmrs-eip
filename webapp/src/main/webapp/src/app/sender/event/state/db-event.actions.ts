import {Action} from "@ngrx/store";
import {DbEventCountAndItems} from "../db-event-count-and-items";

export enum DbEventActionType {
	EVENTS_LOADED = 'EVENTS_LOADED'
}

export class DbEventsLoaded implements Action {

	readonly type = DbEventActionType.EVENTS_LOADED;

	constructor(public countAndItems?: DbEventCountAndItems) {
	}

}

export type DbEventAction = DbEventsLoaded;
