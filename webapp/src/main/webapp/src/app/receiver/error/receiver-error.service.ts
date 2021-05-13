import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {ReceiverError} from "./receiver-error";
import {BaseService} from "../../shared/base.service";
import {ReceiverErrorCountAndItems} from "./receiver-error-count-and-items";

const RESOURCE_NAME = 'receiver/error';

@Injectable({
	providedIn: 'root'
})
export class ReceiverErrorService extends BaseService<ReceiverError> {

	getErrorCountAndItems(): Observable<ReceiverErrorCountAndItems> {
		return this.getCountAndItems(RESOURCE_NAME);
	}

	removeFromQueue(error: ReceiverError): Observable<any> {
		return this.delete(RESOURCE_NAME, error);
	}

}
