import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
	providedIn: 'root'
})
export class SenderDashboardService {

	protected constructor(private httpClient: HttpClient) {
	}

	getSenderSyncCountByStatus(): Observable<Map<string, number>> {
		return this.httpClient.get<Map<string, number>>(environment.apiBaseUrl + "dashboard/sender/countByStatus");
	}

}
