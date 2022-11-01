import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReceiverArchiveComponent } from './receiver-archive.component';

describe('ArchiveComponent', () => {
  let component: ReceiverArchiveComponent;
  let fixture: ComponentFixture<ReceiverArchiveComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReceiverArchiveComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceiverArchiveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
