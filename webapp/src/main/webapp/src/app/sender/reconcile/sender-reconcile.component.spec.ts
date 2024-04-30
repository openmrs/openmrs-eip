import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SenderReconcileComponent} from './sender-reconcile.component';

describe('SenderReconcileComponent', () => {
	let component: SenderReconcileComponent;
	let fixture: ComponentFixture<SenderReconcileComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [SenderReconcileComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SenderReconcileComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
