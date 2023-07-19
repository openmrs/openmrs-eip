import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {Conflict} from "./conflict";
import {BaseService} from "../../shared/base.service";
import {ConflictCountAndItems} from "./confict-count-and-items";
import {environment} from "../../../environments/environment";

const RESOURCE_NAME = 'receiver/conflict';

@Injectable({
	providedIn: 'root'
})
export class ConflictService extends BaseService<Conflict> {

	getConflictCountAndItems(): Observable<ConflictCountAndItems> {
		return this.getCountAndItems(RESOURCE_NAME);
	}

	deleteConflict(conflict: Conflict): Observable<void> {
		return this.delete(RESOURCE_NAME, conflict);
	}

	startVerifyTask(): Observable<void> {
		return this.httpClient.post<void>(environment.apiBaseUrl + RESOURCE_NAME + "/verify/start", null);
	}

	getVerifyTaskStatus(): Observable<boolean> {
		return this.httpClient.get<boolean>(environment.apiBaseUrl + RESOURCE_NAME + "/verify/status");
	}

}
