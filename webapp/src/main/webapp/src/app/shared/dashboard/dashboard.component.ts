import {Component, OnDestroy, OnInit} from "@angular/core";
import {Subscription} from 'rxjs';
import {select, Store} from "@ngrx/store";
import {HttpErrorResponse} from "@angular/common/http";
import {GET_PROPS} from "../../state/app.reducer";
import {SyncMode} from "../sync-mode.enum";
import {GET_DASHBOARD_ERROR} from "./state/dashboard.reducer";

@Component({template: ''})
export abstract class DashboardComponent implements OnInit, OnDestroy {

	propsLoaded?: Subscription;

	dashboardError?: Subscription;

	serverDown = false;

	reload = false;

	constructor(protected store: Store) {
	}

	ngOnInit(): void {
		this.propsLoaded = this.store.pipe(select(GET_PROPS)).subscribe(props => {
			if (props.syncMode == this.getSyncMode() && this.getSyncMode() == SyncMode.SENDER) {
				this.dashboardError = this.store.pipe(select(GET_DASHBOARD_ERROR)).subscribe(error => {
					this.handleLoadError(error);
				});

				this.onInit();
			}
		});
	}

	handleLoadError(error: HttpErrorResponse | undefined): void {
		if (error) {
			if (error.status === 0) {
				this.serverDown = true;
				this.stopSubscriptions();
			} else {
				throw error;
			}
		}
	}

	ngOnDestroy(): void {
		this.stopSubscriptions();
	}

	onInit(): void {
	}

	stopSubscriptions(): void {
		this.propsLoaded?.unsubscribe();
		this.dashboardError?.unsubscribe();
	}

	abstract getSyncMode(): SyncMode;

}
