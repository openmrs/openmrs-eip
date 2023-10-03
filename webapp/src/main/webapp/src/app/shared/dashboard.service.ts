import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {Dashboard} from "./dashboard";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";

@Injectable({
	providedIn: 'root'
})
export class DashboardService {

	protected constructor(private httpClient: HttpClient) {
	}

	getDashboard(): Observable<Dashboard> {
		return this.httpClient.get<Dashboard>(environment.apiBaseUrl + "dashboard");
	}

	getCategories(entityType: string): Observable<Dashboard> {
		return this.httpClient.get<Dashboard>(environment.apiBaseUrl + "dashboard/category", {
			params: {entityType: entityType}
		});
	}

	getCount(entityType: string, category: string, operation: string): Observable<Dashboard> {
		return this.httpClient.get<Dashboard>(environment.apiBaseUrl + "dashboard/count", {
			params: {entityType: entityType, category: category, operation: operation}
		});
	}

}
