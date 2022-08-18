/**
 * Base class for listing components that use datatables
 */
import {Subject} from "rxjs";
import {Component, OnDestroy, ViewChild} from "@angular/core";
import {DataTableDirective} from "angular-datatables";

@Component({template: ''})
export abstract class BaseListingComponent implements OnDestroy {

	dtOptions: DataTables.Settings = {};

	dtTrigger: Subject<any> = new Subject<any>();

	@ViewChild(DataTableDirective, {static: false})
	datatableElement?: DataTableDirective;

	init(): void {
		this.dtOptions = {
			pagingType: 'full_numbers',
			ordering: false
		};
	}

	reRender(): void {
		this.datatableElement?.dtInstance?.then((dtInstance: DataTables.Api) => {
			dtInstance.destroy();
		});

		this.dtTrigger.next();
	}

	ngOnDestroy(): void {
		this.dtTrigger.unsubscribe();
	}

}
