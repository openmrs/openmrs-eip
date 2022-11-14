export abstract class BaseClassPipe {

	getSimpleName(className: any): string {
		return className.substring(className.lastIndexOf('.') + 1);
	}

	beautify(str: string): string {
		return str.replace(/([a-z0-9])([A-Z])/g, '$1 $2');
	}

}
