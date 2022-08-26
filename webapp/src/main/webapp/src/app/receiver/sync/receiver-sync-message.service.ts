import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {ReceiverSyncMessage} from "./receiver-sync-message";
import {ReceiverSyncMessageCountAndItems} from "./receiver-sync-message-count-and-items";
import {BaseService} from "../../shared/base.service";

const RESOURCE_NAME = 'receiver/sync';

@Injectable({
	providedIn: 'root'
})
export class ReceiverSyncMessageService extends BaseService<ReceiverSyncMessage> {

	getSyncMessageCountAndItems(): Observable<ReceiverSyncMessageCountAndItems> {
		return this.getCountAndItems(RESOURCE_NAME);
	}

}
