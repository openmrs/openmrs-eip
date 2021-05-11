/**
 * Base class for listing components that use datatables
 */
import {Subject} from "rxjs";
import {Component, OnDestroy} from "@angular/core";

@Component({template: ''})
export abstract class BaseListingComponent implements OnDestroy {

	dtOptions: DataTables.Settings = {};

	dtTrigger: Subject<any> = new Subject<any>();

	init(): void {
		this.dtOptions = {
			pagingType: 'full_numbers'
		};
	}

	reRender(): void {
		this.dtTrigger.next();
	}

	ngOnDestroy(): void {
		this.dtTrigger.unsubscribe();
	}

}
