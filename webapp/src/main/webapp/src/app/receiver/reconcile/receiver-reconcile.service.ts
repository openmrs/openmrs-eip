import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {ReceiverReconciliation} from "./receiver-reconciliation";

const RESOURCE_NAME = 'receiver/reconcile';

@Injectable({
	providedIn: 'root'
})
export class ReceiverReconcileService {

	protected constructor(protected httpClient: HttpClient) {
	}

	getReconciliation(): Observable<ReceiverReconciliation> {
		return this.httpClient.get<ReceiverReconciliation>(environment.apiBaseUrl + RESOURCE_NAME);
	}


}
