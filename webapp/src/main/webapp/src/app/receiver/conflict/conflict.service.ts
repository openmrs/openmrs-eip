import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {Conflict} from "./conflict";
import {BaseService} from "../../shared/base.service";
import {ConflictCountAndItems} from "./confict-count-and-items";

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

}
