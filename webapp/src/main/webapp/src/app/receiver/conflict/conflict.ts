import {BaseEntity} from "../../shared/base-entity";
import {Site} from "../site";

export class Conflict extends BaseEntity {

	identifier?: string;

	modelClassName?: string;

	operation?: string;

	entityPayload?: string;

	site?: Site;

	resolved: boolean = false;

	messageUuid?: string;

	dateSentBySender?: string;

	dateReceived?: string;

}
