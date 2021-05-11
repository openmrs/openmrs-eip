import {BaseEntity} from "../../shared/base-entity";

export class Conflict extends BaseEntity {

	identifier?: string;

	modelClassName?: string;

	entityPayload?: string;

	resolved: boolean = false;

}
