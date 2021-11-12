import {BaseErrorEntity} from "../../shared/base-error-entity";
import {Site} from "../site";

export class ReceiverError extends BaseErrorEntity {

	identifier?: string;

	modelClassName?: string;

	entityPayload?: string;

	site?: Site;

}
