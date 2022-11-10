import {Component, Input, ViewChild} from '@angular/core';
import {DataTableDirective} from "angular-datatables";

@Component({
	selector: 'grouped-view',
	templateUrl: './grouped-view.component.html'
})
export class GroupedViewComponent {

	@Input()
	columnLabel?: string;

	@Input()
	items?: Map<string, number>;

	@ViewChild(DataTableDirective, {static: false})
	datatableElement?: DataTableDirective;

	dtOptions: DataTables.Settings = {
		pagingType: 'full_numbers',
		ordering: false
	};

}
