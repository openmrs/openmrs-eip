import {BaseErrorEntity} from "../../shared/base-error-entity";

export class ReceiverError extends BaseErrorEntity {

	identifier?: string;

	modelClassName?: string;

	entityPayload?: string;

}
