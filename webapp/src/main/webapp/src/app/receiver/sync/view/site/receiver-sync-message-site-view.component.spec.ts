import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ReceiverSyncMessageSiteViewComponent} from './receiver-sync-message-site-view.component';

describe('ReceiverSyncMessageSiteViewComponent', () => {
	let component: ReceiverSyncMessageSiteViewComponent;
	let fixture: ComponentFixture<ReceiverSyncMessageSiteViewComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ReceiverSyncMessageSiteViewComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReceiverSyncMessageSiteViewComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
