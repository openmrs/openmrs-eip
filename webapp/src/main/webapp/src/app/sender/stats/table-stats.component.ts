import {Component, Input} from "@angular/core";

@Component({
	selector: 'table-stats',
	templateUrl: './table-stats.component.html'
})
export class TableStatsComponent {

	@Input()
	tableStatsMap?: Map<string, any>;

}
