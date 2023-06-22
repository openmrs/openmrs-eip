import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReceiverSyncedMessageGroupViewComponent} from "./receiver-synced-message-group-view.component";


describe('ReceiverSyncedMessageGroupViewComponent', () => {
	let component: ReceiverSyncedMessageGroupViewComponent;
	let fixture: ComponentFixture<ReceiverSyncedMessageGroupViewComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ReceiverSyncedMessageGroupViewComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReceiverSyncedMessageGroupViewComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
