import {ComponentFixture, TestBed} from '@angular/core/testing';
import {SenderSyncMessageComponent} from "./sender-sync-message.component";

describe('SenderSyncMessageComponent', () => {
	let component: SenderSyncMessageComponent;
	let fixture: ComponentFixture<SenderSyncMessageComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [SenderSyncMessageComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SenderSyncMessageComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
