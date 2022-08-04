import {Site} from "../site";
import {BaseEntity} from "../../shared/base-entity";

export class SiteStatus extends BaseEntity {

	siteInfo?: Site;

	lastSyncDate?: string;

}
