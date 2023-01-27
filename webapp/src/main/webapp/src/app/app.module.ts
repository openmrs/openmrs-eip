import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {SharedModule} from "./shared/shared.module";
import {ReceiverModule} from "./receiver/receiver.module";
import {StoreModule} from '@ngrx/store';
import {StoreDevtoolsModule} from '@ngrx/store-devtools';
import {environment} from '../environments/environment';
import {SenderModule} from "./sender/sender.module";
import {appReducer} from "./state/app.reducer";
import {EffectsModule} from "@ngrx/effects";

@NgModule({
	declarations: [
		AppComponent
	],
	imports: [
		SharedModule,
		ReceiverModule,
		SenderModule,
		EffectsModule.forRoot(),
		StoreModule.forRoot({}),
		StoreModule.forFeature('props', appReducer),
		StoreDevtoolsModule.instrument({
			name: "DB Sync",
			maxAge: 25,
			logOnly: environment.production
		})
	],
	providers: [],
	bootstrap: [AppComponent]
})

export class AppModule {
}
