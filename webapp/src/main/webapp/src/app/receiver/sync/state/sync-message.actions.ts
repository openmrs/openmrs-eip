import {Action} from "@ngrx/store";
import {ReceiverSyncMessageCountAndItems} from "../receiver-sync-message-count-and-items";
import {ReceiverSyncMessage} from "../receiver-sync-message";
import {ViewInfo} from "../../shared/view-info";
import {TotalCountAndGroupedItems} from "../../../shared/total-count-and-grouped-items";

export enum SyncMessageActionType {

	SYNC_MSGS_LOADED = 'SYNC_MSGS_LOADED',

	VIEW_SYNC_MSG = 'VIEW_SYNC_MSG',

	CHANGE_SYNC_MSG_VIEW = 'CHANGE_SYNC_MSG_VIEW',

	GROUPED_SYNC_MSGS_LOADED = 'GROUPED_SYNC_MSGS_LOADED'

}

export class SyncMessagesLoaded implements Action {

	readonly type = SyncMessageActionType.SYNC_MSGS_LOADED;

	constructor(public countAndItems?: ReceiverSyncMessageCountAndItems) {
	}

}

export class ViewSyncMessage implements Action {

	readonly type = SyncMessageActionType.VIEW_SYNC_MSG;

	constructor(public message?: ReceiverSyncMessage) {
	}

}

export class ChangeView implements Action {

	readonly type = SyncMessageActionType.CHANGE_SYNC_MSG_VIEW;

	constructor(public viewInfo?: ViewInfo) {
	}

}

export class GroupedSyncMessagesLoaded implements Action {

	readonly type = SyncMessageActionType.GROUPED_SYNC_MSGS_LOADED;

	constructor(public countAndGroupedItems?: TotalCountAndGroupedItems) {
	}

}

export type SyncMessageAction = SyncMessagesLoaded | ViewSyncMessage | ChangeView | GroupedSyncMessagesLoaded;
