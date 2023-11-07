import {Component} from "@angular/core";
import {DashboardComponent} from "../../shared/dashboard/dashboard.component";
import {select} from "@ngrx/store";
import {SyncMode} from "../../shared/sync-mode.enum";
import {FetchCountByStatus, FetchErrorDetails} from "./state/sender.dashboard.actions";
import {GET_ERROR_DETAILS, GET_SYNC_COUNT_BY_STATUS} from "./state/sender.dashboard.reducer";
import {Subscription} from "rxjs";
import {ErrorDetails} from "./error-details";

@Component({
	selector: 'sender-dashboard',
	templateUrl: './sender-dashboard.component.html'
})
export class SenderDashboardComponent extends DashboardComponent {

	countByStatusTimeoutId?: number;

	errorDetailsTimeoutId?: number;

	statusAndCountFetched?: Subscription;

	errorDetailsFetched?: Subscription;

	statusAndCountMap?: Map<string, number>;

	errorDetails?: ErrorDetails;

	reloadCountByStatus = false;

	reloadErrorDetails = false;

	getSyncMode(): SyncMode {
		return SyncMode.SENDER;
	}

	onInit(): void {
		this.statusAndCountFetched = this.store.pipe(select(GET_SYNC_COUNT_BY_STATUS)).subscribe(statusAndCountMap => {
			if (statusAndCountMap) {
				this.statusAndCountMap = statusAndCountMap;
				if (this.reloadCountByStatus) {
					this.fetchSyncCountByStatus(30000);
				}
			}
		});

		this.errorDetailsFetched = this.store.pipe(select(GET_ERROR_DETAILS)).subscribe(errorDetails => {
			if (errorDetails) {
				this.errorDetails = errorDetails;
				if (this.reloadErrorDetails) {
					this.fetchErrorDetails(30000);
				}
			}
		});

		this.fetchSyncCountByStatus(0);
		this.fetchErrorDetails(0);
	}

	fetchSyncCountByStatus(delay: number): void {
		this.countByStatusTimeoutId = setTimeout(() => {
			if (!this.reloadCountByStatus) {
				this.reloadCountByStatus = true;
			}

			this.store.dispatch(new FetchCountByStatus());
		}, delay);
	}

	fetchErrorDetails(delay: number): void {
		this.errorDetailsTimeoutId = setTimeout(() => {
			if (!this.reloadErrorDetails) {
				this.reloadErrorDetails = true;
			}

			this.store.dispatch(new FetchErrorDetails());
		}, delay);
	}

	getErrorCount(): number | undefined {
		if (this.errorDetails && this.errorDetails.exceptionCountMap) {
			return Object.keys(this.errorDetails.exceptionCountMap).length;
		}

		return undefined;
	}

	stopSubscriptions(): void {
		clearTimeout(this.countByStatusTimeoutId);
		clearTimeout(this.errorDetailsTimeoutId);
		this.statusAndCountFetched?.unsubscribe();
		this.errorDetailsFetched?.unsubscribe();
		super.stopSubscriptions();
	}

}
