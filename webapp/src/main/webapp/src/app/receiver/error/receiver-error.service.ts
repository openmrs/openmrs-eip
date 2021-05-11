import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {ReceiverError} from "./receiver-error";
import {BaseService} from "../../shared/base.service";

const RESOURCE_NAME = 'receiver/error';

@Injectable({
	providedIn: 'root'
})
export class ReceiverErrorService extends BaseService<ReceiverError> {

	getAllErrors(): Observable<ReceiverError[]> {
		return this.getAll(RESOURCE_NAME);
	}

	removeFromQueue(error: ReceiverError): Observable<any> {
		return this.delete(RESOURCE_NAME, error);
	}

}
