import {BaseErrorEntity} from "../../shared/base-error-entity";
import {Site} from "../site";

export class ReceiverSyncMessage extends BaseErrorEntity {

	identifier?: string;

	modelClassName?: string;

	messageUuid?: string;

	entityPayload?: string;

	site?: Site;

	dateSentBySender?: string;

}
