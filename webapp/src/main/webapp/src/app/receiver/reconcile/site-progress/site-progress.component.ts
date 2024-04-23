import {Component, Input} from '@angular/core';
import {BaseListingComponent} from "../../../shared/base-listing.component";

@Component({
	selector: 'site-progress',
	templateUrl: './site-progress.component.html'
})
export class SiteProgressComponent extends BaseListingComponent {

	constructor() {
		super();
	}

	@Input()
	siteProgress: any;

	@Input()
	tableCount?: any;

	getSiteName(value: any): string {
		return value.substr(value.indexOf('^') + 1);
	}

	castToInt(value: any): number {
		return value;
	}

}
