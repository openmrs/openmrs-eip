import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Reconciliation} from "../../shared/reconciliation";
import {environment} from "../../../environments/environment";
import {SenderTableReconcile} from "./sender-table-reconcile";

const RESOURCE_NAME = 'sender/reconcile';

@Injectable({
	providedIn: 'root'
})
export class SenderReconcileService {

	protected constructor(protected httpClient: HttpClient) {
	}

	getReconciliation(): Observable<Reconciliation> {
		return this.httpClient.get<Reconciliation>(environment.apiBaseUrl + RESOURCE_NAME);
	}

	getIncompleteTableReconciliations(): Observable<SenderTableReconcile[]> {
		return this.httpClient.get<SenderTableReconcile[]>(environment.apiBaseUrl + RESOURCE_NAME + '/tablereconcile');
	}
}
