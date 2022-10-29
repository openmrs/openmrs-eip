import {BaseEntity} from "../../shared/base-entity";

export class SenderSyncArchive extends BaseEntity {

	tableName?: string;

	identifier?: string;

	operation?: string;

	messageUuid?: string;

	requestUuid?: string;

	dateSent?: string;

	eventDate?: string;

	snapshot?: boolean;

	dateReceivedByReceiver?: string;

}
