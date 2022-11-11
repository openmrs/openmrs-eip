import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {ReceiverSyncMessage} from "./receiver-sync-message";
import {ReceiverSyncMessageCountAndItems} from "./receiver-sync-message-count-and-items";
import {BaseService} from "../../shared/base.service";
import {TotalCountAndGroupedItems} from "../../shared/total-count-and-grouped-items";

const RESOURCE_NAME = 'receiver/sync';

@Injectable({
	providedIn: 'root'
})
export class ReceiverSyncMessageService extends BaseService<ReceiverSyncMessage> {

	getSyncMessageCountAndItems(): Observable<ReceiverSyncMessageCountAndItems> {
		return this.getCountAndItems(RESOURCE_NAME);
	}

	getTotalCountAndSyncMessagesGroupedBySite(groupProperty: string): Observable<TotalCountAndGroupedItems> {
		return this.getTotalCountAndGroupedItems(RESOURCE_NAME, groupProperty);
	}

}
