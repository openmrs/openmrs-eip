import {ErrorHandler, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BrowserModule} from "@angular/platform-browser";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {DataTablesModule} from "angular-datatables";
import {ConfirmDialogComponent} from "./dialogs/confirm.component";
import {HttpErrorInterceptor} from "./http-error.interceptor";
import {ModelClassPipe} from "./pipes/model-class.pipe";
import {GlobalErrorHandler} from "./global-error.handler";
import {ClassNamePipe} from "./pipes/class-name.pipe";
import {GroupViewComponent} from "./view/group/group-view.component";
import {EffectsModule} from "@ngrx/effects";
import {DashboardEffects} from "./state/dashboard.effects";
import {StoreModule} from "@ngrx/store";
import {dashboardReducer} from "./state/dashboard.reducer";
import {ServerDownComponent} from "./server-down.component";
import {MessageDialogComponent} from "./dialogs/message.component";
import {QueueDataEffects} from "./dashboard/queue-data/state/queue-data.effects";
import {QueueDataComponent} from "./dashboard/queue-data/queue-data.component";
import {queueDataReducer} from "./dashboard/queue-data/state/queue-data.reducer";


@NgModule({
	declarations: [
		ConfirmDialogComponent,
		MessageDialogComponent,
		GroupViewComponent,
		ClassNamePipe,
		ModelClassPipe,
		ServerDownComponent,
		QueueDataComponent
	],
	imports: [
		CommonModule,
		DataTablesModule,
		EffectsModule.forFeature([DashboardEffects, QueueDataEffects]),
		StoreModule.forFeature("dashboard", dashboardReducer),
		StoreModule.forFeature("syncQueueData", queueDataReducer)
	],
	exports: [
		CommonModule,
		BrowserModule,
		HttpClientModule,
		NgbModule,
		DataTablesModule,
		GroupViewComponent,
		ClassNamePipe,
		ModelClassPipe,
		ServerDownComponent,
		QueueDataComponent
	],
	providers: [
		{
			provide: HTTP_INTERCEPTORS,
			useClass: HttpErrorInterceptor,
			multi: true
		},
		{
			provide: ErrorHandler,
			useClass: GlobalErrorHandler
		},
		ModelClassPipe
	]
})

export class SharedModule {
}
