import {TestBed} from '@angular/core/testing';

import {ReceiverErrorService} from './receiver-error.service';

describe('ReceiverErrorService', () => {
	let service: ReceiverErrorService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(ReceiverErrorService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
