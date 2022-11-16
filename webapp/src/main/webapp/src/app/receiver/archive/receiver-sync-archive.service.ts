import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {BaseService} from "../../shared/base.service";
import {ReceiverSyncArchive} from './receiver-sync-archive';
import {ReceiverSyncArchiveCountAndItems} from './receiver-sync-archive-count-and-items';
import {TotalCountAndGroupedItems} from "../../shared/total-count-and-grouped-items";

const RESOURCE_NAME = 'receiver/archive';

@Injectable({
	providedIn: 'root'
})
export class ReceiverSyncArchiveService extends BaseService<ReceiverSyncArchive> {

	getSyncArchiveCountAndItems(): Observable<ReceiverSyncArchiveCountAndItems> {
		return this.getCountAndItems(RESOURCE_NAME);
	}

	searchByDateReceived(startDate: string, endDate: string): Observable<ReceiverSyncArchiveCountAndItems> {
		return this.searchCountAndItems(RESOURCE_NAME, {startDate: startDate, endDate: endDate})
	}

	getTotalCountAndGroupedArchives(groupProperty: string): Observable<TotalCountAndGroupedItems> {
		return this.getTotalCountAndGroupedItems(RESOURCE_NAME, groupProperty);
	}

}
