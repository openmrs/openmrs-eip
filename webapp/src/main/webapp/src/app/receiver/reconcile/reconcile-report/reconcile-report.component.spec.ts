import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ReconcileReportComponent} from './reconcile-report.component';

describe('ReconcileReportComponent', () => {
	let component: ReconcileReportComponent;
	let fixture: ComponentFixture<ReconcileReportComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ReconcileReportComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReconcileReportComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
