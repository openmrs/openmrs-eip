import {ComponentFixture, TestBed} from '@angular/core/testing';
import {SiteStatusComponent} from './site-status.component';

describe('SiteStatusComponent', () => {
	let component: SiteStatusComponent;
	let fixture: ComponentFixture<SiteStatusComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [SiteStatusComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SiteStatusComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
