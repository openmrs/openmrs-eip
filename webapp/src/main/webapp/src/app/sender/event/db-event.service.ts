import {Injectable} from "@angular/core";
import {BaseService} from "../../shared/base.service";
import {Observable} from "rxjs";
import {DbEvent} from "./db-event";
import {DbEventCountAndItems} from "./db-event-count-and-items";

const RESOURCE_NAME = 'sender/event';

@Injectable({
	providedIn: 'root'
})
export class DbEventService extends BaseService<DbEvent> {

	getEventCountAndItems(): Observable<DbEventCountAndItems> {
		return this.getCountAndItems(RESOURCE_NAME);
	}

}
