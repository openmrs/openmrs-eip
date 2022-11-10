import {Component, OnInit} from '@angular/core';

@Component({
	selector: 'receiver-sync-msg-site-view',
	templateUrl: './receiver-sync-message-site-view.component.html'
})
export class ReceiverSyncMessageSiteViewComponent implements OnInit {

	columnLabel = $localize`:@@common-health-facility:Health Facility`;

	items?: Map<string, number> = new Map;

	constructor() {
	}

	ngOnInit(): void {
	}

}
