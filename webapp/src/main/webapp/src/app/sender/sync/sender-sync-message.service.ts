import {Injectable} from "@angular/core";
import {BaseService} from "../../shared/base.service";
import {Observable} from "rxjs";
import {SenderSyncMessageCountAndItems} from "./sender-sync-message-count-and-items";
import {SenderSyncMessage} from "./sender-sync-message";

const RESOURCE_NAME = 'sender/sync';

@Injectable({
	providedIn: 'root'
})
export class SenderSyncMessageService extends BaseService<SenderSyncMessage> {

	getMessageCountAndItems(): Observable<SenderSyncMessageCountAndItems> {
		return this.getCountAndItems(RESOURCE_NAME);
	}

}
