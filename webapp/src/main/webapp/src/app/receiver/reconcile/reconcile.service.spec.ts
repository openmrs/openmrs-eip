import {TestBed} from '@angular/core/testing';
import {ReceiverReconcileService} from "./receiver-reconcile.service";

describe('ReconcileService', () => {
	let service: ReceiverReconcileService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(ReceiverReconcileService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
