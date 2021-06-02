import {ErrorHandler} from "@angular/core";
import {throwError} from "rxjs";

export class GlobalErrorHandler implements ErrorHandler {

	handleError(error: any) {
		alert('An unexpected error occurred');
		throw throwError(error);
	}

}
