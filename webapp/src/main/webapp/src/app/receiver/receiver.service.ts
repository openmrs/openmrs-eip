import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {Site} from "./site";

const BASE_RECEIVER_URL = environment.apiBaseUrl + 'receiver/';

@Injectable({
	providedIn: 'root'
})
export class ReceiverService {

	protected constructor(protected httpClient: HttpClient) {
	}

	getSites(): Observable<Site[]> {
		return this.httpClient.get<Site[]>(BASE_RECEIVER_URL + 'site');
	}

}
