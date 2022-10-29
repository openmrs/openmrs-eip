import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { BaseEntity } from "./base-entity";
import { environment } from "../../environments/environment";
import { BaseCountAndItems } from "./base-count-and-items";

@Injectable({
	providedIn: 'root'
})
export abstract class BaseService<T extends BaseEntity> {

	protected constructor(private httpClient: HttpClient) {
	}

	getCountAndItems(resource: string): Observable<BaseCountAndItems<T>> {
		return this.httpClient.get<BaseCountAndItems<T>>(environment.apiBaseUrl + resource);
	}

	update(resource: string, entity: T): Observable<T> {
		return this.httpClient.patch<T>(environment.apiBaseUrl + resource + '/' + entity.id, entity);
	}

	delete(resource: string, entity: T): Observable<any> {
		return this.httpClient.delete<T>(environment.apiBaseUrl + resource + '/' + entity.id);
	}

	searchCountAndItems(resource: string, paramsData: {
		[param: string]: any;
	}): Observable<BaseCountAndItems<T>> {
		return this.httpClient.get<BaseCountAndItems<T>>(environment.apiBaseUrl + resource, {
			params: paramsData
		});
	}
}
