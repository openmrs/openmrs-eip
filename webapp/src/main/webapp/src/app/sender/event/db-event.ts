import {Event} from "../event";
import {BaseEntity} from "../../shared/base-entity";

export class DbEvent extends BaseEntity {

	primaryKeyId?: any;

	event?: Event;

}
