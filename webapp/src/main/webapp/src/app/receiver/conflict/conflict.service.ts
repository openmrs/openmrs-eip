import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {Conflict} from "./conflict";
import {BaseService} from "../../shared/base.service";
import {ConflictCountAndItems} from "./confict-count-and-items";
import {environment} from "../../../environments/environment";
import {Diff} from "./diff";

const RESOURCE_NAME = 'receiver/conflict';

@Injectable({
	providedIn: 'root'
})
export class ConflictService extends BaseService<Conflict> {

	getConflictCountAndItems(): Observable<ConflictCountAndItems> {
		return this.getCountAndItems(RESOURCE_NAME);
	}

	getDiff(conflict: Conflict): Observable<Diff> {
		return this.httpClient.get<Diff>(environment.apiBaseUrl + RESOURCE_NAME + '/' + conflict.id + '/diff');
	}

	resolveConflict(conflict: Conflict, resolutionData: any): Observable<void> {
		return this.httpClient.post<void>(environment.apiBaseUrl + RESOURCE_NAME + '/' + conflict.id + '/resolve',
			resolutionData, {
				headers: {'Content-Type': 'application/json'}
			});
	}

	startVerifyTask(): Observable<void> {
		return this.httpClient.post<void>(environment.apiBaseUrl + RESOURCE_NAME + '/verify/start', null);
	}

	getVerifyTaskStatus(): Observable<boolean> {
		return this.httpClient.get<boolean>(environment.apiBaseUrl + RESOURCE_NAME + '/verify/status');
	}

	startResolverTask(): Observable<void> {
		return this.httpClient.post<void>(environment.apiBaseUrl + RESOURCE_NAME + '/task/resolver/start', null);
	}

	getResolverTaskStatus(): Observable<boolean> {
		return this.httpClient.get<boolean>(environment.apiBaseUrl + RESOURCE_NAME + '/task/resolver/status');
	}

}
