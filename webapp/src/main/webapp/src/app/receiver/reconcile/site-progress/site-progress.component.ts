import {Component, Input} from '@angular/core';
import {BaseListingComponent} from "../../../shared/base-listing.component";
import {LoadReceiverTableReconciliations} from "../state/receiver-reconcile.actions";
import {Store} from "@ngrx/store";

@Component({
	selector: 'site-progress',
	templateUrl: './site-progress.component.html'
})
export class SiteProgressComponent extends BaseListingComponent {

	constructor(private store: Store) {
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

	showIncompleteTables(key: any): void {
		let siteId: any = key.substr(0, key.indexOf('^'));
		this.store.dispatch(new LoadReceiverTableReconciliations(siteId));
	}

}
