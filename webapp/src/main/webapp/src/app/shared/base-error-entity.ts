import {BaseEntity} from "./base-entity";

export abstract class BaseErrorEntity extends BaseEntity {

	message?: string;

	causeMessage?: string;

	attemptCount?: number;

	dateCreated?: string;

}
