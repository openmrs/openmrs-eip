import {Action} from "@ngrx/store";
import {ReceiverSyncArchiveCountAndItems} from "../receiver-sync-archive-count-and-items";
import {ViewInfo} from "../../shared/view-info";
import {TotalCountAndGroupedItems} from "../../../shared/total-count-and-grouped-items";
import {DateRange} from "../../../shared/date-range";

export enum ReceiverArchiveActionType {

	SYNC_ARCHIVES_LOADED = 'SYNC_ARCHIVES_LOADED',

	CHANGE_ARCHIVE_VIEW = 'CHANGE_ARCHIVE_VIEW',

	GROUPED_ARCHIVES_LOADED = 'GROUPED_ARCHIVES_LOADED',

	FILTER_ARCHIVES = 'FILTER_ARCHIVES'

}

export class ReceiverArchiveLoaded implements Action {

	readonly type = ReceiverArchiveActionType.SYNC_ARCHIVES_LOADED;

	constructor(public countAndItems?: ReceiverSyncArchiveCountAndItems) {
	}

}

export class ChangeArchivesView implements Action {

	readonly type = ReceiverArchiveActionType.CHANGE_ARCHIVE_VIEW;

	constructor(public viewInfo?: ViewInfo) {
	}

}

export class GroupedArchivesLoaded implements Action {

	readonly type = ReceiverArchiveActionType.GROUPED_ARCHIVES_LOADED;

	constructor(public countAndGroupedItems?: TotalCountAndGroupedItems) {
	}

}

export class FilterArchives implements Action {

	readonly type = ReceiverArchiveActionType.FILTER_ARCHIVES;

	constructor(public filterDateRange?: DateRange) {
	}

}

export type ReceiverArchiveAction = ReceiverArchiveLoaded | ChangeArchivesView | GroupedArchivesLoaded | FilterArchives;
