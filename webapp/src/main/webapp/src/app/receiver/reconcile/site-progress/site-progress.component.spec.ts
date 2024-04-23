import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SiteProgressComponent} from './site-progress.component';

describe('SiteProgressComponent', () => {
	let component: SiteProgressComponent;
	let fixture: ComponentFixture<SiteProgressComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [SiteProgressComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SiteProgressComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
