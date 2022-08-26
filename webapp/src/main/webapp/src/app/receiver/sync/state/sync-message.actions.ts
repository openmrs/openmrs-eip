import {Action} from "@ngrx/store";
import {ReceiverSyncMessageCountAndItems} from "../receiver-sync-message-count-and-items";
import {ReceiverSyncMessage} from "../receiver-sync-message";

export enum SyncMessageActionType {
	MSGS_LOADED = 'MSGS_LOADED',
	VIEW_MSG = 'VIEW_MSG'
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

export type SyncMessageAction = SyncMessagesLoaded | ViewSyncMessage;
