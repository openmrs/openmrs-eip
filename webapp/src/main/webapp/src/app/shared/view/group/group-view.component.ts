import {Component, Input, ViewChild} from '@angular/core';
import {DataTableDirective} from "angular-datatables";

@Component({
	selector: 'group-view',
	templateUrl: './group-view.component.html'
})
export class GroupViewComponent {

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
