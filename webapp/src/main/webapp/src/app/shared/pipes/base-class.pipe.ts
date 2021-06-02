export abstract class BaseClassPipe {

	getSimpleName(className: any): string {
		return className.substring(className.lastIndexOf('.') + 1);
	}

}
