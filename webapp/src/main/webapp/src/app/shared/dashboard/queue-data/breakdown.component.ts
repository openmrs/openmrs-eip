import {Component, Input, OnInit} from "@angular/core";
import {SyncOperation} from "../../sync-operation.enum";
import {ModelClassPipe} from "../../pipes/model-class.pipe";

@Component({
	selector: 'breakdown',
	templateUrl: './breakdown.component.html'
})
export class BreakdownComponent implements OnInit {

	readonly SyncOperation = SyncOperation;

	@Input()
	isReceiver?: boolean;

	@Input()
	categories?: string[];

	categoryLabel?: string;

	@Input()
	categoryAndCountsMap?: Map<string, Map<SyncOperation, number | null>>;

	constructor(private classPipe: ModelClassPipe) {
	}

	public getCategoryDisplay(value: string): string {
		if (this.isReceiver) {
			return this.classPipe.transform(value);
		}

		return value;
	}

	ngOnInit(): void {
		if (this.isReceiver) {
			this.categoryLabel = $localize`:@@common-entity:Entity`;
		} else {
			this.categoryLabel = $localize`:@@common-table-name:Table Name`;
		}
	}

}
