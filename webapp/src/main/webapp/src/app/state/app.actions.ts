import {Action} from "@ngrx/store";
import {AppProperties} from "../receiver/shared/app-properties";

export enum AppActionType {
	PROPERTIES_LOADED = 'PROPERTIES_LOADED'
}

export class PropertiesLoaded implements Action {

	readonly type = AppActionType.PROPERTIES_LOADED;

	constructor(public properties: AppProperties) {
	}

}

export type AppAction = PropertiesLoaded;
