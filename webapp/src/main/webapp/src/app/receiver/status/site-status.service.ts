import {Injectable} from "@angular/core";
import {BaseService} from "../../shared/base.service";
import {Observable} from "rxjs";
import {SiteStatus} from "./site-status";
import {SiteStatusCountAndItems} from "./site-status-count-and-items";

const RESOURCE_NAME = 'receiver/status';

@Injectable({
	providedIn: 'root'
})
export class SiteStatusService extends BaseService<SiteStatus> {

	getStatusCountAndItems(): Observable<SiteStatusCountAndItems> {
		return this.getCountAndItems(RESOURCE_NAME);
	}

}
