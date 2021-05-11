import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {BaseService} from "../../shared/base.service";
import {SenderError} from "./sender-error";

const RESOURCE_NAME = 'sender/error';

@Injectable({
	providedIn: 'root'
})
export class SenderErrorService extends BaseService<SenderError> {

	getAllErrors(): Observable<SenderError[]> {
		return this.getAll(RESOURCE_NAME);
	}

	removeFromQueue(error: SenderError): Observable<any> {
		return this.delete(RESOURCE_NAME, error);
	}

}
