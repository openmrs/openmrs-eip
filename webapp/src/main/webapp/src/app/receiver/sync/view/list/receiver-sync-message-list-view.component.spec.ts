import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ReceiverSyncMessageListViewComponent} from './receiver-sync-message-list-view.component';

describe('ReceiverSyncMessageListViewComponent', () => {
	let component: ReceiverSyncMessageListViewComponent;
	let fixture: ComponentFixture<ReceiverSyncMessageListViewComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ReceiverSyncMessageListViewComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReceiverSyncMessageListViewComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
