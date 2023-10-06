import {SyncOperation} from "../../sync-operation.enum";

export class QueueData {

	count?: number;

	categories?: string[];

	categoryAndCounts?: Map<string, Map<SyncOperation, number>>;

}
