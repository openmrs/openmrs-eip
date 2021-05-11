import {TestBed} from '@angular/core/testing';

import {ConflictService} from './conflict.service';

describe('ConflictService', () => {
	let service: ConflictService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(ConflictService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
