import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReceiverReconcileComponent} from "./receiver-reconcile.component";

describe('ReconcileComponent', () => {
	let component: ReceiverReconcileComponent;
	let fixture: ComponentFixture<ReceiverReconcileComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ReceiverReconcileComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReceiverReconcileComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
