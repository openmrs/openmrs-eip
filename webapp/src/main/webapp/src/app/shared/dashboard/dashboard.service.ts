import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {Dashboard} from "./dashboard";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {SyncOperation} from "../sync-operation.enum";

@Injectable({
	providedIn: 'root'
})
export class DashboardService {

	protected constructor(private httpClient: HttpClient) {
	}

	getDashboard(): Observable<Dashboard> {
		return this.httpClient.get<Dashboard>(environment.apiBaseUrl + "dashboard");
	}

	getCategories(entityType: string | undefined): Observable<string[]> {
		return this.httpClient.get<string[]>(environment.apiBaseUrl + "dashboard/category", {
			params: {
				entityType: entityType ? entityType : ''
			}
		});
	}

	getCount(entityType: string | undefined, category?: string, operation?: SyncOperation): Observable<number> {
		let requestParams;
		if (!category || !operation) {
			requestParams = {
				entityType: entityType ? entityType : ''
			}
		} else {
			requestParams = {
				entityType: entityType ? entityType : '',
				category: category,
				operation: operation
			}
		}

		return this.httpClient.get<number>(environment.apiBaseUrl + "dashboard/count", {
			params: requestParams
		});
	}

}
