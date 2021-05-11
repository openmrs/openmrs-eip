import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {Conflict} from "./conflict";
import {BaseService} from "../../shared/base.service";

const RESOURCE_NAME = 'receiver/conflict';

@Injectable({
	providedIn: 'root'
})
export class ConflictService extends BaseService<Conflict> {

	getAllConflicts(): Observable<Conflict[]> {
		return this.getAll(RESOURCE_NAME);
	}

	updateConflict(conflict: Conflict): Observable<Conflict> {
		return this.update(RESOURCE_NAME, conflict);
	}

}
