import {NgModule} from '@angular/core';
import {SenderErrorComponent} from './error/sender-error.component';
import {SharedModule} from "../shared/shared.module";
import {StoreModule} from "@ngrx/store";
import {senderErrorReducer} from "./error/state/error.reducer";
import {SenderComponent} from './sender.component';
import {TableStatsComponent} from "./stats/table-stats.component";
import {SenderDashboardComponent} from "./dashboard/sender-dashboard.component";
import {DbEventComponent} from "./event/db-event.component";
import {dbEventReducer} from "./event/state/db-event.reducer";
import {senderSyncMessageReducer} from "./sync/state/sender-sync-message.reducer";
import {SenderSyncMessageComponent} from "./sync/sender-sync-message.component";
import { SenderArchiveComponent } from './archive/sender-archive.component';
import { senderArchiveReducer } from './archive/state/sender-archive.reducer';
import { FormsModule } from '@angular/forms';


@NgModule({
	declarations: [
		SenderComponent,
		SenderErrorComponent,
		SenderDashboardComponent,
		TableStatsComponent,
		DbEventComponent,
		SenderSyncMessageComponent,
		SenderArchiveComponent,
	],
	imports: [
		SharedModule,
		StoreModule.forFeature('senderErrorQueue', senderErrorReducer),
		StoreModule.forFeature('eventQueue', dbEventReducer),
		StoreModule.forFeature('syncQueue', senderSyncMessageReducer),
		StoreModule.forFeature('senderArchiveQueue', senderArchiveReducer),
		FormsModule,
	], exports: [SenderComponent]
})

export class SenderModule {
}
