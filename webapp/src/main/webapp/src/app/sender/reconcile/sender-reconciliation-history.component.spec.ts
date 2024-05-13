import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SenderReconciliationHistoryComponent} from './sender-reconciliation-history.component';

describe('SenderReconciliationHistoryComponent', () => {
	let component: SenderReconciliationHistoryComponent;
	let fixture: ComponentFixture<SenderReconciliationHistoryComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [SenderReconciliationHistoryComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SenderReconciliationHistoryComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
