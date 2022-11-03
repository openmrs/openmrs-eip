import {BaseErrorEntity} from "../../shared/base-error-entity";
import {Site} from "../site";

export class ReceiverError extends BaseErrorEntity {

	identifier?: string;

	modelClassName?: string;

	operation?: string;

	entityPayload?: string;

	site?: Site;

	messageUuid?: string;

	dateSentBySender?: string;

	dateReceived?: string;

}
