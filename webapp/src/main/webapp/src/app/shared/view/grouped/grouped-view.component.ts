import {Component, Input, OnInit} from '@angular/core';

@Component({
	selector: 'grouped-view',
	templateUrl: './grouped-view.component.html'
})
export class GroupedViewComponent {

	@Input()
	propertyColumnLabel?: string;

	@Input()
	propertyCountMap?: Map<string, number>;

}
