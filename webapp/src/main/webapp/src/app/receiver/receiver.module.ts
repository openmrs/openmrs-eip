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
import {ReceiverSyncedMessageComponent} from "./synced/receiver-synced-message.component";
import {ReceiverSyncedMessageListViewComponent} from "./synced/view/list/receiver-synced-message-list-view.component";
import {
	ReceiverSyncedMessageGroupViewComponent
} from "./synced/view/group/receiver-synced-message-group-view.component";
import {syncedMessageReducer} from "./synced/state/synced-message.reducer";
import {ReconcileComponent} from './reconcile/reconcile.component';
import {EffectsModule} from "@ngrx/effects";
import {ReceiverReconcileEffects} from "./reconcile/state/receiver-reconcile.effects";
import {receiverReconcileReducer} from "./reconcile/state/receiver-reconcile.reducer";

@NgModule({
	declarations: [
		ReceiverComponent,
		ReceiverErrorComponent,
		ConflictComponent,
		SiteStatusComponent,
		ReceiverSyncMessageComponent,
		ReceiverSyncedMessageComponent,
		ReceiverArchiveComponent,
		EntityStatsComponent,
		ReceiverSyncMessageListViewComponent,
		ReceiverSyncMessageGroupViewComponent,
		ReceiverSyncedMessageListViewComponent,
		ReceiverSyncedMessageGroupViewComponent,
		ReceiverArchiveListViewComponent,
		ReceiverArchiveGroupViewComponent,
		ReceiverDashboardComponent,
		ReconcileComponent
	],
	imports: [
		SharedModule,
		StoreModule.forFeature('conflictQueue', conflictReducer),
		StoreModule.forFeature('receiverErrorQueue', receiverErrorReducer),
		StoreModule.forFeature('siteStatuses', siteStatusReducer),
		StoreModule.forFeature('syncMsgQueue', syncMessageReducer),
		StoreModule.forFeature('syncedMsgQueue', syncedMessageReducer),
		StoreModule.forFeature('receiverArchiveQueue', syncArchiveReducer),
		StoreModule.forFeature('receiverReconcile', receiverReconcileReducer),
		EffectsModule.forFeature([ReceiverReconcileEffects]),
		FormsModule
	], exports: [ReceiverComponent]
})

export class ReceiverModule {
}
