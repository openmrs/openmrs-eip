import {Component, OnInit} from '@angular/core';

@Component({
	selector: 'receiver-sync-messages',
	templateUrl: './receiver-sync-message.component.html'
})
export class ReceiverSyncMessageComponent implements OnInit {

	count?: number;

	viewLabel?: string;

	view?: string;

	ngOnInit(): void {
		this.changeView('list');
	}

	changeView(view: string) {
		this.view = view;
		switch (this.view) {
			case 'list':
				this.viewLabel = $localize`:@@common-list:List`;
				break;
			case 'site':
				this.viewLabel = $localize`:@@common-health-facility:Health Facility`;
				break;
			case 'entity':
				this.viewLabel = $localize`:@@common-entity:Entity`;
				break;
		}
	}

	updateCount(count: number): void {
		this.count = count;
	}

}
