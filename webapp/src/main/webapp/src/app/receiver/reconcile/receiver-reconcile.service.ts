import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {ReceiverReconcileProgress} from "./receiver-reconcile-progress";
import {ReceiverTableReconcile} from "./receiver-table-reconcile";
import {Reconciliation} from "../../shared/reconciliation";

const RESOURCE_NAME = 'receiver/reconcile';

@Injectable({
	providedIn: 'root'
})
export class ReceiverReconcileService {

	protected constructor(protected httpClient: HttpClient) {
	}

	getReconciliation(): Observable<Reconciliation> {
		return this.httpClient.get<Reconciliation>(environment.apiBaseUrl + RESOURCE_NAME);
	}

	startReconciliation(): Observable<Reconciliation> {
		return this.httpClient.post<Reconciliation>(environment.apiBaseUrl + RESOURCE_NAME, null);
	}

	getReconciliationProgress(): Observable<ReceiverReconcileProgress> {
		return this.httpClient.get<ReceiverReconcileProgress>(environment.apiBaseUrl + RESOURCE_NAME + '/progress');
	}

	getSiteProgress(): Observable<any> {
		return this.httpClient.get<any>(environment.apiBaseUrl + RESOURCE_NAME + '/siteprogress');
	}

	getIncompleteTableReconciliations(siteId: number): Observable<ReceiverTableReconcile[]> {
		return this.httpClient.get<ReceiverTableReconcile[]>(environment.apiBaseUrl + RESOURCE_NAME + '/tablereconcile/' + siteId);
	}

}
