import {Site} from "../site";
import {BaseEntity} from "../../shared/base-entity";
import {Outcome} from "./outcome.enum";

export class ReceiverSyncedMessage extends BaseEntity {

	identifier?: string;

	modelClassName?: string;

	operation?: string;

	messageUuid?: string;

	entityPayload?: string;

	site?: Site;

	dateSentBySender?: string;

	dateReceived?: string;

	outcome?: Outcome;

	responseSent?: boolean;

	cached?: boolean;

	evictedFromCache?: boolean;

	indexed?: boolean;

	searchIndexUpdated?: boolean;

}
