import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ReceiverSyncMessageComponent} from './receiver-sync-message.component';

describe('ReceiverSyncMessageComponent', () => {
	let component: ReceiverSyncMessageComponent;
	let fixture: ComponentFixture<ReceiverSyncMessageComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ReceiverSyncMessageComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReceiverSyncMessageComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
