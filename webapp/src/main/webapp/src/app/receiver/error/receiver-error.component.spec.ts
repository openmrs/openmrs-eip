import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ReceiverErrorComponent} from './receiver-error.component';

describe('ReceiverErrorComponent', () => {
	let component: ReceiverErrorComponent;
	let fixture: ComponentFixture<ReceiverErrorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ReceiverErrorComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReceiverErrorComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
