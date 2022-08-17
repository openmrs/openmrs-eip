import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SenderSyncMessagesComponent } from './sender-sync-messages.component';

describe('SenderSyncMessagesComponent', () => {
  let component: SenderSyncMessagesComponent;
  let fixture: ComponentFixture<SenderSyncMessagesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SenderSyncMessagesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SenderSyncMessagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
