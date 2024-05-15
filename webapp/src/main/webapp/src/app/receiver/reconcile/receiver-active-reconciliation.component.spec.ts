import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ReceiverActiveReconciliationComponent} from './receiver-active-reconciliation.component';

describe('ReceiverActiveReconciliationComponent', () => {
	let component: ReceiverActiveReconciliationComponent;
	let fixture: ComponentFixture<ReceiverActiveReconciliationComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ReceiverActiveReconciliationComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReceiverActiveReconciliationComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
