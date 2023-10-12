import {Component, Input, OnInit} from "@angular/core";
import {SyncOperation} from "../../sync-operation.enum";
import {ModelClassPipe} from "../../pipes/model-class.pipe";
import {SyncMode} from "../../sync-mode.enum";

@Component({
	selector: 'breakdown',
	templateUrl: './breakdown.component.html'
})
export class BreakdownComponent implements OnInit {

	readonly SyncOperation = SyncOperation;

	@Input()
	syncMode?: SyncMode;

	@Input()
	categories?: string[];

	categoryLabel?: string;

	@Input()
	categoryAndCountsMap?: Map<string, Map<SyncOperation, number>>;

	constructor(private classPipe: ModelClassPipe) {
	}

	ngOnInit(): void {
		if (this.syncMode == SyncMode.RECEIVER) {
			this.categoryLabel = $localize`:@@common-entity:Entity`;
		} else {
			this.categoryLabel = $localize`:@@common-table-name:Table Name`;
		}
	}

	public getCategoryDisplay(value: string): string {
		if (this.syncMode == SyncMode.RECEIVER) {
			return this.classPipe.transform(value);
		}

		return value;
	}

	public getCountDisplay(count: number | null | undefined): number | string {
		if (!count) {
			return '';
		}

		return count;
	}

}
