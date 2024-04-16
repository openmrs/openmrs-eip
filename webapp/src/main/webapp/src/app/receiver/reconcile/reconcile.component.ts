import {Component, OnInit} from '@angular/core';
import {ConflictService} from "../conflict/conflict.service";
import {Store} from "@ngrx/store";

@Component({
	selector: 'receiver-reconcile',
	templateUrl: './reconcile.component.html'
})
export class ReconcileComponent implements OnInit {

	constructor(
		private service: ConflictService,
		private store: Store) {
	}

	ngOnInit(): void {
	}

}
