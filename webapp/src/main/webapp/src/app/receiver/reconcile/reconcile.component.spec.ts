import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ReconcileComponent} from './reconcile.component';

describe('ReconcileComponent', () => {
  let component: ReconcileComponent;
  let fixture: ComponentFixture<ReconcileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
		declarations: [ReconcileComponent]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReconcileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
