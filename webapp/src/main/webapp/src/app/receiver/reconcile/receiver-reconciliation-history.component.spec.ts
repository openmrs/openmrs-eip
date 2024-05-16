import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ReceiverReconciliationHistoryComponent} from './receiver-reconciliation-history.component';

describe('ReceiverReconciliationHistoryComponent', () => {
	let component: ReceiverReconciliationHistoryComponent;
	let fixture: ComponentFixture<ReceiverReconciliationHistoryComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ReceiverReconciliationHistoryComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReceiverReconciliationHistoryComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
