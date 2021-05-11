import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BrowserModule} from "@angular/platform-browser";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {DataTablesModule} from "angular-datatables";
import {ConfirmDialogComponent} from "./dialogs/confirm.component";


@NgModule({
	declarations: [ConfirmDialogComponent],
	exports: [
		CommonModule,
		BrowserModule,
		HttpClientModule,
		NgbModule,
		DataTablesModule
	]
})

export class SharedModule {
}
