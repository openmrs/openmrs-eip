import {Component, OnDestroy, OnInit} from '@angular/core';
import {AppService} from "./app.service";
import {PropertiesLoaded} from "./state/app.actions";
import {Subscription} from "rxjs";
import {GET_PROPS} from "./state/app.reducer";
import {select, Store} from "@ngrx/store";
import {SyncMode} from "./receiver/shared/sync-mode.enum";

@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {

	syncMode?: SyncMode;

	loadedSubscription?: Subscription;

	constructor(private appService: AppService, private store: Store) {
	}

	ngOnInit(): void {
		this.loadedSubscription = this.store.pipe(select(GET_PROPS)).subscribe(props => {
			this.syncMode = props.syncMode
		});

		this.loadProperties();
	}

	loadProperties(): void {
		this.appService.getAppProperties().subscribe(props => {
			this.store.dispatch(new PropertiesLoaded(props))
		})
	}

	ngOnDestroy(): void {
		this.loadedSubscription?.unsubscribe();
	}

}
