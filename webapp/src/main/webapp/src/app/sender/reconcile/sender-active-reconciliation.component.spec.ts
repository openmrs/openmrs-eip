import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SenderActiveReconciliationComponent} from './sender-active-reconciliation.component';

describe('SenderActiveReconciliationComponent', () => {
	let component: SenderActiveReconciliationComponent;
	let fixture: ComponentFixture<SenderActiveReconciliationComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [SenderActiveReconciliationComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SenderActiveReconciliationComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
