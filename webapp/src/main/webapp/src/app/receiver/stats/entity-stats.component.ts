import {Component, Input} from "@angular/core";

@Component({
	selector: 'entity-stats',
	templateUrl: './entity-stats.component.html'
})
export class EntityStatsComponent {

	@Input()
	entityStatsMap?: Map<string, any>;

}
