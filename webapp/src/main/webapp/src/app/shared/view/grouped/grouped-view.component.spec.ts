import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GroupedViewComponent} from './grouped-view.component';

describe('GroupedViewComponent', () => {
	let component: GroupedViewComponent;
	let fixture: ComponentFixture<GroupedViewComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [GroupedViewComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(GroupedViewComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
