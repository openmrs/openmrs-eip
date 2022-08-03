import {Action} from "@ngrx/store";
import {SiteStatusCountAndItems} from "../site-status-count-and-items";

export enum SiteStatusActionType {
	STATUTES_LOADED = 'STATUSES_LOADED'
}

export class SiteStatusesLoaded implements Action {

	readonly type = SiteStatusActionType.STATUTES_LOADED;

	constructor(public countAndItems?: SiteStatusCountAndItems) {
	}

}

export type SiteStatusAction = SiteStatusesLoaded;
