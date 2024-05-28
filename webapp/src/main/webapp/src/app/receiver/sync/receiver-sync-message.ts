import {Site} from "../site";
import {BaseEntity} from "../../shared/base-entity";

export class ReceiverSyncMessage extends BaseEntity {

	identifier?: string;

	modelClassName?: string;

	operation?: string;

	messageUuid?: string;

	entityPayload?: string;

	site?: Site;

	dateSentBySender?: string;

	dateReceived?: string;

}
