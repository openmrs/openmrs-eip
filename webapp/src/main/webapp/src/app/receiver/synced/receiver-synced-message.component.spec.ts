import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ReceiverSyncedMessageComponent} from './receiver-synced-message.component';

describe('ReceiverSyncedMessageComponent', () => {
	let component: ReceiverSyncedMessageComponent;
	let fixture: ComponentFixture<ReceiverSyncedMessageComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ReceiverSyncedMessageComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReceiverSyncedMessageComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
