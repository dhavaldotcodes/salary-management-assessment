import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ShellComponent } from './layout/shell.component';
import { InsightsComponent } from './features/insights/insights.component';
import { EmployeeListComponent } from './features/employees/employee-list.component';
import { EmployeeFormComponent } from './features/employees/employee-form.component';

@NgModule({
  declarations: [
    AppComponent,
    ShellComponent,
    InsightsComponent,
    EmployeeListComponent,
    EmployeeFormComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
