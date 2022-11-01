import {Action} from "@ngrx/store";
import { ReceiverSyncArchiveCountAndItems } from "../receiver-sync-archive-count-and-items";

export enum ReceiverArchiveActionType {
	SYNC_ARCHIVE_LOADED = 'SYNC_ARCHIVE_LOADED',
}

export class ReceiverArchiveLoaded implements Action {

	readonly type = ReceiverArchiveActionType.SYNC_ARCHIVE_LOADED;

	constructor(public countAndItems?: ReceiverSyncArchiveCountAndItems) {
	}

}

export type ReceiverArchiveAction = ReceiverArchiveLoaded;
