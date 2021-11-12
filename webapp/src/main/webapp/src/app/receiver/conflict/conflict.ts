import {BaseEntity} from "../../shared/base-entity";
import {Site} from "../site";

export class Conflict extends BaseEntity {

	identifier?: string;

	modelClassName?: string;

	entityPayload?: string;

	site?: Site;

	resolved: boolean = false;

}
