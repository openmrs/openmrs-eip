import {Action} from "@ngrx/store";
import {ReceiverSyncedMessageCountAndItems} from "../receiver-synced-message-count-and-items";
import {ReceiverSyncedMessage} from "../receiver-synced-message";
import {ViewInfo} from "../../shared/view-info";
import {TotalCountAndGroupedItems} from "../../../shared/total-count-and-grouped-items";

export enum SyncedMessageActionType {

	SYNCED_MSGS_LOADED = 'SYNCED_MSGS_LOADED',

	VIEW_SYNCED_MSG = 'VIEW_SYNCED_MSG',

	CHANGE_SYNCED_MSG_VIEW = 'CHANGE_SYNCED_MSG_VIEW',

	GROUPED_SYNCED_MSGS_LOADED = 'GROUPED_SYNCED_MSGS_LOADED'

}

export class SyncedMessagesLoaded implements Action {

	readonly type = SyncedMessageActionType.SYNCED_MSGS_LOADED;

	constructor(public countAndItems?: ReceiverSyncedMessageCountAndItems) {
	}

}

export class ViewSyncedMessage implements Action {

	readonly type = SyncedMessageActionType.VIEW_SYNCED_MSG;

	constructor(public message?: ReceiverSyncedMessage) {
	}

}

export class ChangeSyncedMessageView implements Action {

	readonly type = SyncedMessageActionType.CHANGE_SYNCED_MSG_VIEW;

	constructor(public viewInfo?: ViewInfo) {
	}

}

export class GroupedSyncedMessagesLoaded implements Action {

	readonly type = SyncedMessageActionType.GROUPED_SYNCED_MSGS_LOADED;

	constructor(public countAndGroupedItems?: TotalCountAndGroupedItems) {
	}

}

export type SyncedMessageAction = SyncedMessagesLoaded | ViewSyncedMessage | ChangeSyncedMessageView | GroupedSyncedMessagesLoaded;
