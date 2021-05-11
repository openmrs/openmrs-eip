import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SenderErrorComponent} from './sender-error.component';

describe('SenderErrorComponent', () => {
	let component: SenderErrorComponent;
	let fixture: ComponentFixture<SenderErrorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [SenderErrorComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SenderErrorComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
