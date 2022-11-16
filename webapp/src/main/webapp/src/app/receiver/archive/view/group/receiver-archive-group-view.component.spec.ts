import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReceiverArchiveGroupViewComponent} from "./receiver-archive-group-view.component";

describe('ReceiverArchiveSiteViewComponent', () => {
	let component: ReceiverArchiveGroupViewComponent;
	let fixture: ComponentFixture<ReceiverArchiveGroupViewComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ReceiverArchiveGroupViewComponent]
		})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReceiverArchiveGroupViewComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
