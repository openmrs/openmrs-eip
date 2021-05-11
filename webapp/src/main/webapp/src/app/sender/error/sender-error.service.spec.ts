import { TestBed } from '@angular/core/testing';

import { SenderErrorService } from './sender-error.service';

describe('SenderErrorService', () => {
  let service: SenderErrorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SenderErrorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
