import {SyncOperation} from "../../sync-operation.enum";

export class QueueData {

	count?: number | null;

	categories?: string[];

	categoryAndCounts?: Map<string, Map<SyncOperation, number | null>>;

}
