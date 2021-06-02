import {Pipe, PipeTransform} from "@angular/core";
import {BaseClassPipe} from "./base-class.pipe";

@Pipe({name: 'modelClass'})
export class ModelClassPipe extends BaseClassPipe implements PipeTransform {

	transform(className: any, ...args: any[]): any {
		let simpleClassName = this.getSimpleName(className);
		return simpleClassName.substring(0, simpleClassName.lastIndexOf('Model'));
	}

}
