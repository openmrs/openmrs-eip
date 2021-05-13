import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {BaseService} from "../../shared/base.service";
import {SenderError} from "./sender-error";
import {SenderErrorCountAndItems} from "./sender-error-count-and-items";

const RESOURCE_NAME = 'sender/error';

@Injectable({
	providedIn: 'root'
})
export class SenderErrorService extends BaseService<SenderError> {

	getErrorCountAndItems(): Observable<SenderErrorCountAndItems> {
		return this.getCountAndItems(RESOURCE_NAME);
	}

	removeFromQueue(error: SenderError): Observable<any> {
		return this.delete(RESOURCE_NAME, error);
	}

}
