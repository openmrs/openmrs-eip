import {BaseEntity} from "../../shared/base-entity";

export class VerifyTaskStatus extends BaseEntity {

	running: boolean = false;

	lastUpdated?: Date;

	constructor(running: boolean, lastUpdated: Date) {
		super();
		this.running = running;
		this.lastUpdated = lastUpdated;
	}

}
