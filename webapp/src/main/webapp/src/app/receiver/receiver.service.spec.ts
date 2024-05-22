import {TestBed} from '@angular/core/testing';

import {ReceiverService} from './receiver.service';

describe('ReceiverService', () => {
	let service: ReceiverService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(ReceiverService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
