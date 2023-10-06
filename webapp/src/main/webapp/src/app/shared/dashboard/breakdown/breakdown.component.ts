import {Component, Input} from "@angular/core";
import {SyncOperation} from "../../sync-operation.enum";

@Component({
	selector: 'breakdown',
	templateUrl: './breakdown.component.html'
})
export class BreakdownComponent {

	readonly SyncOperation = SyncOperation;

	@Input()
	categoryAndCountsMap?: Map<string, Map<SyncOperation, number>>;

}
