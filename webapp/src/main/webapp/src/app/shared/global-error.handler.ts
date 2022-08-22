import {ErrorHandler} from "@angular/core";

export class GlobalErrorHandler implements ErrorHandler {

	handleError(error: any) {
		document.open();
		document.write('<h1 style="color:#ff0000">An unexpected error occurred</h1>');
		document.close();
	}

}
