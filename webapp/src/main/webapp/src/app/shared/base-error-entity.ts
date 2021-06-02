import {BaseEntity} from "./base-entity";

export abstract class BaseErrorEntity extends BaseEntity {

	exceptionType?: string;

	message?: string;

	attemptCount?: number;

	dateCreated?: string;

}
