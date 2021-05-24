import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../environments/environment";
import {Observable} from "rxjs";
import {AppProperties} from "./app.component";

@Injectable({
	providedIn: 'root'
})
export class AppService {

	constructor(private httpClient: HttpClient) {
	}

	getAppProperties(): Observable<AppProperties> {
		return this.httpClient.get<AppProperties>(environment.apiBaseUrl + "properties");
	}

}
