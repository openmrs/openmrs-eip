import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {Dashboard} from "./dashboard";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";

@Injectable({
	providedIn: 'root'
})
export class DashboardService {

	protected constructor(private httpClient: HttpClient) {
	}

	getDashboard(): Observable<Dashboard> {
		return this.httpClient.get<Dashboard>(environment.apiBaseUrl + "dashboard");
	}

	getCategories(entityType: string): Observable<string[]> {
		return this.httpClient.get<string[]>(environment.apiBaseUrl + "dashboard/category", {
			params: {
				entityType: entityType
			}
		});
	}

	getCount(entityType: any, category: any, operation: any): Observable<number> {
		let requestParams;
		if (!category || !operation) {
			requestParams = {
				entityType: entityType
			}
		} else {
			requestParams = {
				entityType: entityType,
				category: category,
				operation: operation
			}
		}

		return this.httpClient.get<number>(environment.apiBaseUrl + "dashboard/count", {
			params: requestParams
		});
	}

}
