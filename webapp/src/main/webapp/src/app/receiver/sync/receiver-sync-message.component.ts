import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {BaseListingComponent} from "../../shared/base-listing.component";
import {NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {Subscription} from "rxjs";
import {ReceiverSyncMessage} from "./receiver-sync-message";

@Component({
	selector: 'receiver-sync-messages',
	templateUrl: './receiver-sync-message.component.html'
})
export class ReceiverSyncMessageComponent extends BaseListingComponent implements OnInit {

	count?: number;

	syncMessages?: ReceiverSyncMessage[];

	modalRef?: NgbModalRef;

	parsedEntityPayLoad?: any;

	@ViewChild('detailsTemplate')
	detailsRef?: ElementRef;

	viewSubscription?: Subscription;

	loadedSubscription?: Subscription;

	constructor() {
		super();
	}

	ngOnInit(): void {
	}

	viewMessage(msg: ReceiverSyncMessage): void {

	}

	closeDetailsDialog(): void {
		this.modalRef?.close();
	}

}
