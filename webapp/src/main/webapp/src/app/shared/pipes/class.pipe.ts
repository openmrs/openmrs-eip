import {Pipe, PipeTransform} from "@angular/core";

@Pipe({name: 'modelClass'})
export class ClassPipe implements PipeTransform {

	transform(className: any, ...args: any[]): any {
		return className?.substring(className.lastIndexOf('.') + 1, className.lastIndexOf('Model'));
	}

}
