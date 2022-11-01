import {BaseEntity} from "src/app/shared/base-entity";
import {Site} from "../site";

export class ReceiverSyncArchive extends BaseEntity {

	identifier?: string;

	modelClassName?: string;

	messageUuid?: string;

	site?: Site;

	dateSentBySender?: string;

	dateReceived?: string;

	snapshot?: boolean;

}
