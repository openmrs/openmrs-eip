import {BaseErrorEntity} from "../../shared/base-error-entity";
import {Event} from "../event";

export class SenderError extends BaseErrorEntity {

	route?: string;

	event?: Event;

}
