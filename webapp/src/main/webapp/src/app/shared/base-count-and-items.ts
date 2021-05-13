import {BaseEntity} from "./base-entity";

export abstract class BaseCountAndItems<T extends BaseEntity> {

	count?: number;

	items?: T[];

}
