import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ReceiverArchiveListViewComponent} from './receiver-archive-list-view.component';

describe('ReceiverArchiveListViewComponent', () => {
	let component: ReceiverArchiveListViewComponent;
	let fixture: ComponentFixture<ReceiverArchiveListViewComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ReceiverArchiveListViewComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReceiverArchiveListViewComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
