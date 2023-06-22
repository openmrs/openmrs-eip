import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ReceiverSyncedMessageListViewComponent} from './receiver-synced-message-list-view.component';

describe('ReceiverSyncedMessageListViewComponent', () => {
	let component: ReceiverSyncedMessageListViewComponent;
	let fixture: ComponentFixture<ReceiverSyncedMessageListViewComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ReceiverSyncedMessageListViewComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReceiverSyncedMessageListViewComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
