import {NgModule} from '@angular/core';
import {ReceiverErrorComponent} from './error/receiver-error.component';
import {ConflictComponent} from './conflict/conflict.component';
import {SharedModule} from "../shared/shared.module";
import {StoreModule} from "@ngrx/store";
import {conflictReducer} from "./conflict/state/conflict.reducer";
import {receiverErrorReducer} from "./error/state/error.reducer";
import {ReceiverComponent} from './receiver.component';
import {SiteStatusComponent} from "./status/site-status.component";
import {siteStatusReducer} from "./status/state/site-status.reducer";
import {ReceiverSyncMessageComponent} from './sync/receiver-sync-message.component';
import {syncMessageReducer} from "./sync/state/sync-message.reducer";
import {ReceiverArchiveComponent} from './archive/receiver-archive.component';
import {syncArchiveReducer} from './archive/state/receiver-archive.reducer';
import {FormsModule} from '@angular/forms';
import {ReceiverSyncMessageListViewComponent} from "./sync/view/list/receiver-sync-message-list-view.component";
import {ReceiverSyncMessageGroupViewComponent} from './sync/view/group/receiver-sync-message-group-view.component'
import {ReceiverArchiveGroupViewComponent} from "./archive/view/group/receiver-archive-group-view.component";
import {ReceiverArchiveListViewComponent} from "./archive/view/list/receiver-archive-list-view.component";
import {ReceiverDashboardComponent} from "./dashboard/receiver-dashboard.component";
import {EntityStatsComponent} from "./stats/entity-stats.component";

@NgModule({
	declarations: [
		ReceiverComponent,
		ReceiverErrorComponent,
		ConflictComponent,
		SiteStatusComponent,
		ReceiverSyncMessageComponent,
		ReceiverArchiveComponent,
		EntityStatsComponent,
		ReceiverSyncMessageListViewComponent,
		ReceiverSyncMessageGroupViewComponent,
		ReceiverArchiveListViewComponent,
		ReceiverArchiveGroupViewComponent,
		ReceiverDashboardComponent
	],
	imports: [
		SharedModule,
		StoreModule.forFeature('conflictQueue', conflictReducer),
		StoreModule.forFeature('receiverErrorQueue', receiverErrorReducer),
		StoreModule.forFeature('siteStatuses', siteStatusReducer),
		StoreModule.forFeature('syncMsgQueue', syncMessageReducer),
		StoreModule.forFeature('receiverArchiveQueue', syncArchiveReducer),
		FormsModule
	], exports: [ReceiverComponent]
})

export class ReceiverModule {
}
