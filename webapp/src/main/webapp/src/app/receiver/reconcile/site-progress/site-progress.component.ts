import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {BaseListingComponent} from "../../../shared/base-listing.component";
import {
	LoadReceiverTableReconciliations,
	ReceiverTableReconciliationsLoaded
} from "../state/receiver-reconcile.actions";
import {select, Store} from "@ngrx/store";
import {NgbModal, NgbModalOptions, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {ReceiverTableReconcile} from "../receiver-table-reconcile";
import {Subscription} from "rxjs";
import {GET_RECEIVER_TABLE_RECONCILES} from "../state/receiver-reconcile.reducer";

@Component({
	selector: 'site-progress',
	templateUrl: './site-progress.component.html'
})
export class SiteProgressComponent extends BaseListingComponent implements OnInit {

	constructor(
		private store: Store,
		private modalService: NgbModal) {
		super();
	}

	@Input()
	siteProgress: any;

	@Input()
	tableCount?: any;

	tableReconciliations?: ReceiverTableReconcile[];

	modalRef?: NgbModalRef;

	@ViewChild('tableRecTemplate')
	tableRecRef?: ElementRef;

	loadedTableRecsSubscription?: Subscription;

	ngOnInit(): void {
		this.loadedTableRecsSubscription = this.store.pipe(select(GET_RECEIVER_TABLE_RECONCILES)).subscribe(
			tableRecs => {
				this.tableReconciliations = tableRecs;
				if (this.tableReconciliations) {
					this.showTableReconciliationsDialog();
				}
			}
		);
	}

	getSiteName(value: any): string {
		return value.substr(value.indexOf('^') + 1);
	}

	castToInt(value: any): number {
		return value;
	}

	showIncompleteTables(key: any): void {
		let siteId: any = key.substr(0, key.indexOf('^'));
		this.store.dispatch(new LoadReceiverTableReconciliations(siteId));
	}

	getLastBatchReceivedLabel(lastBatchReceived?: boolean): string {
		return lastBatchReceived ? $localize`:@@common-yes:Yes` : $localize`:@@common-no:No`;
	}

	showTableReconciliationsDialog(): void {
		const dialogConfig: NgbModalOptions = {
			backdrop: 'static',
			size: 'xl',
			centered: true,
			scrollable: true
		}

		this.modalRef = this.modalService.open(this.tableRecRef, dialogConfig);
		this.modalRef.closed.subscribe(() => {
			this.store.dispatch(new ReceiverTableReconciliationsLoaded(undefined));
		});
	}

	ngOnDestroy(): void {
		this.loadedTableRecsSubscription?.unsubscribe();
	}

}
