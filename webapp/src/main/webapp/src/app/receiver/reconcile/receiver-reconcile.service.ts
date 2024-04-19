import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {ReceiverReconciliation} from "./receiver-reconciliation";
import {ReceiverReconcileProgress} from "./receiver-reconcile-progress";

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

	startReconciliation(): Observable<ReceiverReconciliation> {
		return this.httpClient.post<ReceiverReconciliation>(environment.apiBaseUrl + RESOURCE_NAME, null);
	}

	getReconciliationProgress(): Observable<ReceiverReconcileProgress> {
		return this.httpClient.get<ReceiverReconcileProgress>(environment.apiBaseUrl + RESOURCE_NAME + '/progress');
	}

}
