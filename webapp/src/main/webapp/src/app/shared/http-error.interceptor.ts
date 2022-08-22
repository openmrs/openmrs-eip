import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from "rxjs/operators";

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

	intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		return next.handle(request).pipe(catchError(this.errorHandler));
	}

	errorHandler(errorResponse: HttpErrorResponse) {
		if (errorResponse.status === 401) {
			window.location.href = "/login";
		} else {
			console.error('An http error occurred with status: ' + errorResponse.status);
		}

		return throwError(errorResponse)
	}

}
