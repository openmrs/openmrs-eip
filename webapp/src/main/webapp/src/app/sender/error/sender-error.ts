import {BaseErrorEntity} from "../../shared/base-error-entity";

class Event {

	identifier?: string;

	tableName?: string;

	operation?: string;

	snapshot?: boolean;

}

export class SenderError extends BaseErrorEntity {

	route?: string;

	event?: Event;

}
