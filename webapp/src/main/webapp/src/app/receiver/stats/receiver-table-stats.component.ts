import {Component, Input} from "@angular/core";

@Component({
	selector: 'receiver-table-stats',
	templateUrl: './table-stats.component.html'
})
export class ReceiverTableStatsComponent {

	@Input()
	tableStatsMap?: Map<string, any>;

}
