import {Conflict} from "./conflict";

export class Diff {

	exclusions?: string[];

	conflict?: Conflict;

	currentState?: any;

	newState?: any;

	properties?: string[];

	additions?: string[];

	modifications?: string[];

	removals?: string[];

}
