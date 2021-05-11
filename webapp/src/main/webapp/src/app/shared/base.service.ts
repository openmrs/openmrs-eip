import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {BaseEntity} from "./base-entity";
import {environment} from "../../environments/environment";

@Injectable({
	providedIn: 'root'
})
export abstract class BaseService<T extends BaseEntity> {

	protected constructor(private httpClient: HttpClient) {
	}

	getAll(resource: string): Observable<T[]> {
		return this.httpClient.get<T[]>(environment.apiBaseUrl + resource);
	}

	update(resource: string, entity: T): Observable<T> {
		return this.httpClient.patch<T>(environment.apiBaseUrl + resource + '/' + entity.id, entity);
	}

	delete(resource: string, entity: T): Observable<any> {
		return this.httpClient.delete<T>(environment.apiBaseUrl + resource + '/' + entity.id);
	}

}
