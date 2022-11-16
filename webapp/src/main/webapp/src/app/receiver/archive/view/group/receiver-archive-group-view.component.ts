import {Component, Input, OnInit} from '@angular/core';
import {ViewInfo} from "../../../shared/view-info";
import {Subscription} from "rxjs";
import {select, Store} from "@ngrx/store";
import {ModelClassPipe} from "../../../../shared/pipes/model-class.pipe";
import {View} from "../../../shared/view.enum";
import {GET_GRP_PROP_COUNT_MAP} from "../../state/receiver-archive.reducer";
import {ReceiverSyncArchiveService} from "../../receiver-sync-archive.service";
import {GroupedArchivesLoaded} from "../../state/receiver-archive.actions";

@Component({
	selector: 'receiver-archive-group-view',
	templateUrl: './receiver-archive-group-view.component.html'
})
export class ReceiverArchiveGroupViewComponent implements OnInit {

	@Input()
	viewInfo?: ViewInfo;

	groupPropertyCountMap?: Map<string, number>;

	loadedSubscription?: Subscription;

	constructor(private service: ReceiverSyncArchiveService, private store: Store, private classPipe: ModelClassPipe) {
	}

	ngOnInit(): void {
		this.loadedSubscription = this.store.pipe(select(GET_GRP_PROP_COUNT_MAP)).subscribe(
			map => {
				if (this.viewInfo?.view == View.ENTITY) {
					let transformedMap = new Map<string, number>();
					if (map) {
						Object.entries(map).forEach((entry) => {
							transformedMap?.set(this.classPipe.transform(entry[0]), entry[1]);
						});
					}

					this.groupPropertyCountMap = transformedMap;
				} else {
					this.groupPropertyCountMap = map;
				}
			}
		);

		let groupBy: string = 'site';
		if (this.viewInfo?.view == View.ENTITY) {
			groupBy = 'modelClassName';
		}

		this.service.getTotalCountAndGroupedArchives(groupBy).subscribe(countAndGroupedItems => {
			this.store.dispatch(new GroupedArchivesLoaded(countAndGroupedItems));
		});
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
	}

}
