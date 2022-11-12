import {Action} from "@ngrx/store";
import {ReceiverSyncMessageCountAndItems} from "../receiver-sync-message-count-and-items";
import {ReceiverSyncMessage} from "../receiver-sync-message";
import {ViewInfo} from "../../shared/view-info";
import {TotalCountAndGroupedItems} from "../../../shared/total-count-and-grouped-items";

export enum SyncMessageActionType {

	MSGS_LOADED = 'MSGS_LOADED',

	VIEW_MSG = 'VIEW_MSG',

	CHANGE_VIEW = 'CHANGE_VIEW',

	GROUPED_MSGS_LOADED = 'GROUPED_MSGS_LOADED'

}

export class SyncMessagesLoaded implements Action {

	readonly type = SyncMessageActionType.MSGS_LOADED;

	constructor(public countAndItems?: ReceiverSyncMessageCountAndItems) {
	}

}

export class ViewSyncMessage implements Action {

	readonly type = SyncMessageActionType.VIEW_MSG;

	constructor(public message?: ReceiverSyncMessage) {
	}

}

export class ChangeView implements Action {

	readonly type = SyncMessageActionType.CHANGE_VIEW;

	constructor(public viewInfo?: ViewInfo) {
	}

}

export class GroupedSyncMessagesLoaded implements Action {

	readonly type = SyncMessageActionType.GROUPED_MSGS_LOADED;

	constructor(public countAndGroupedItems?: TotalCountAndGroupedItems) {
	}

}

export type SyncMessageAction = SyncMessagesLoaded | ViewSyncMessage | ChangeView | GroupedSyncMessagesLoaded;
