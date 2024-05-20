import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {ReceiverReconcileProgress} from "./receiver-reconcile-progress";
import {ReceiverTableReconcile} from "./receiver-table-reconcile";
import {Reconciliation} from "../../shared/reconciliation";
import {ReconcileTableSummary} from "./reconcile-table-summary";

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

	getHistory(): Observable<Reconciliation[]> {
		return this.httpClient.get<Reconciliation[]>(environment.apiBaseUrl + RESOURCE_NAME + '/history');
	}

	getReport(): Observable<[]> {
		return this.httpClient.get<[]>(environment.apiBaseUrl + RESOURCE_NAME + '/report');
	}

	getTableSummariesBySite(siteIdentifier: string): Observable<ReconcileTableSummary[]> {
		return this.httpClient.get<ReconcileTableSummary[]>(environment.apiBaseUrl + RESOURCE_NAME + '/report', {
			params: {"siteId": siteIdentifier}
		});
	}

}
