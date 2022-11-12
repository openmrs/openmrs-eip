import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReceiverSyncMessageGroupViewComponent} from "./receiver-sync-message-group-view.component";


describe('ReceiverSyncMessageGroupViewComponent', () => {
	let component: ReceiverSyncMessageGroupViewComponent;
	let fixture: ComponentFixture<ReceiverSyncMessageGroupViewComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ReceiverSyncMessageGroupViewComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReceiverSyncMessageGroupViewComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
