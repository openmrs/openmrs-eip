import {Component, Input, OnInit} from "@angular/core";
import {ViewInfo} from "./view-info";
import {Observable, Subscription} from "rxjs";
import {Action, DefaultProjectorFn, MemoizedSelector, select, Store} from "@ngrx/store";
import {ModelClassPipe} from "../../shared/pipes/model-class.pipe";
import {View} from "./view.enum";
import {TotalCountAndGroupedItems} from "../../shared/total-count-and-grouped-items";

@Component({template: ''})
export abstract class BaseReceiverGroupViewComponent implements OnInit {

	@Input()
	viewInfo?: ViewInfo;

	groupPropertyCountMap?: Map<string, number>;

	loadedSubscription?: Subscription;

	protected constructor(protected store: Store, private classPipe: ModelClassPipe) {
	}

	ngOnInit(): void {
		this.loadedSubscription = this.store.pipe(select(this.getSelector())).subscribe(
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

		this.getTotalCountAndGroupedItems(groupBy).subscribe(countAndGroupedItems => {
			this.store.dispatch(this.createLoadAction(countAndGroupedItems));
		});
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
	}

	abstract getTotalCountAndGroupedItems(groupProperty: string): Observable<TotalCountAndGroupedItems>;

	abstract getSelector(): MemoizedSelector<object, Map<string, number> | undefined, DefaultProjectorFn<Map<string, number> | undefined>>;

	abstract createLoadAction(countAndGroupedItems: TotalCountAndGroupedItems): Action;

}
