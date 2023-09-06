import {Conflict} from "./conflict";

export class Diff {

	conflict?: Conflict;

	currentState?: any;

	newState?: any;

	properties?: string[];

	additions?: string[];

	modifications?: string[];

	removals?: string[];

}
