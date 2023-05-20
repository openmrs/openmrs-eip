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

	updateConflict(conflict: Conflict): Observable<Conflict> {
		return this.update(RESOURCE_NAME, conflict);
	}

	verifyConflicts(): Observable<number> {
		return this.httpClient.get<number>(environment.apiBaseUrl + RESOURCE_NAME + "/verify");
	}

	cleanConflicts(): Observable<number> {
		return this.httpClient.post<number>(environment.apiBaseUrl + RESOURCE_NAME + "/clean", null);
	}

}
