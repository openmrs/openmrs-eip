import {Site} from "../site";
import {BaseEntity} from "../../shared/base-entity";

export class ReceiverSyncedMessage extends BaseEntity {

	identifier?: string;

	modelClassName?: string;

	operation?: string;

	messageUuid?: string;

	entityPayload?: string;

	site?: Site;

	dateSentBySender?: string;

	dateReceived?: string;

	outcome?: string;

	responseSent?: boolean;

	cached?: boolean;

	evictedFromCache?: string;

	indexed?: boolean;

	searchIndexUpdated?: string;

}
