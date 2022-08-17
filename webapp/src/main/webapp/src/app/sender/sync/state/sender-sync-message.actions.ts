import {Action} from "@ngrx/store";
import {SenderSyncMessageCountAndItems} from "../sender-sync-message-count-and-items";

export enum SenderSyncMessageActionType {
	MSGS_LOADED = 'MSGS_LOADED'
}

export class SenderSyncMessagesLoaded implements Action {

	readonly type = SenderSyncMessageActionType.MSGS_LOADED;

	constructor(public countAndItems?: SenderSyncMessageCountAndItems) {
	}

}

export type SenderSyncMessageAction = SenderSyncMessagesLoaded;
