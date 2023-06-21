import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {ReceiverSyncedMessage} from "./receiver-synced-message";
import {ReceiverSyncedMessageCountAndItems} from "./receiver-synced-message-count-and-items";
import {BaseService} from "../../shared/base.service";
import {TotalCountAndGroupedItems} from "../../shared/total-count-and-grouped-items";

const RESOURCE_NAME = 'receiver/synced';

@Injectable({
	providedIn: 'root'
})
export class ReceiverSyncedMessageService extends BaseService<ReceiverSyncedMessage> {

	getSyncedMessageCountAndItems(): Observable<ReceiverSyncedMessageCountAndItems> {
		return this.getCountAndItems(RESOURCE_NAME);
	}

	getTotalCountAndGroupedSyncedMessages(groupProperty: string): Observable<TotalCountAndGroupedItems> {
		return this.getTotalCountAndGroupedItems(RESOURCE_NAME, groupProperty);
	}

}
