import {Component, Input} from "@angular/core";

@Component({
	selector: 'table-stats',
	templateUrl: './table-stats.component.html'
})
export class TableStatsComponent {

	@Input()
	tableName?: any;

	@Input()
	stats?: any;

}
