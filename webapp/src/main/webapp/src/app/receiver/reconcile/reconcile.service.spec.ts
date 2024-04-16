import {TestBed} from '@angular/core/testing';

import {ReconcileService} from './reconcile.service';

describe('ReconcileService', () => {
	let service: ReconcileService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(ReconcileService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
