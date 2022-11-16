import {Action} from "@ngrx/store";
import {ReceiverSyncArchiveCountAndItems} from "../receiver-sync-archive-count-and-items";
import {ViewInfo} from "../../shared/view-info";
import {TotalCountAndGroupedItems} from "../../../shared/total-count-and-grouped-items";

export enum ReceiverArchiveActionType {

	SYNC_ARCHIVES_LOADED = 'SYNC_ARCHIVES_LOADED',

	CHANGE_ARCHIVE_VIEW = 'CHANGE_ARCHIVE_VIEW',

	GROUPED_ARCHIVES_LOADED = 'GROUPED_ARCHIVES_LOADED'

}

export class ReceiverArchiveLoaded implements Action {

	readonly type = ReceiverArchiveActionType.SYNC_ARCHIVES_LOADED;

	constructor(public countAndItems?: ReceiverSyncArchiveCountAndItems) {
	}

}

export class ChangeView implements Action {

	readonly type = ReceiverArchiveActionType.CHANGE_ARCHIVE_VIEW;

	constructor(public viewInfo?: ViewInfo) {
	}

}

export class GroupedArchivesLoaded implements Action {

	readonly type = ReceiverArchiveActionType.GROUPED_ARCHIVES_LOADED;

	constructor(public countAndGroupedItems?: TotalCountAndGroupedItems) {
	}

}

export type ReceiverArchiveAction = ReceiverArchiveLoaded | ChangeView | GroupedArchivesLoaded;
