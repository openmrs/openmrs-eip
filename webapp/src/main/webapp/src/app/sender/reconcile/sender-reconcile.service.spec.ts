import {TestBed} from '@angular/core/testing';

import {SenderReconcileService} from './sender-reconcile.service';

describe('SenderReconcileService', () => {
	let service: SenderReconcileService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(SenderReconcileService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
