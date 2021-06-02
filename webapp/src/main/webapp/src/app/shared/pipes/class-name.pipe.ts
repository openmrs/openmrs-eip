import {Pipe, PipeTransform} from "@angular/core";
import {BaseClassPipe} from "./base-class.pipe";

@Pipe({name: 'classname'})
export class ClassNamePipe extends BaseClassPipe implements PipeTransform {

	transform(className: any, ...args: any[]): any {
		return this.getSimpleName(className);
	}

}
