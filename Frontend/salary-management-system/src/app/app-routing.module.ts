import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ShellComponent } from './layout/shell.component';
import { InsightsComponent } from './features/insights/insights.component';
import { EmployeeListComponent } from './features/employees/employee-list.component';
import { EmployeeFormComponent } from './features/employees/employee-form.component';

const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'insights' },
      { path: 'insights', component: InsightsComponent },
      { path: 'employees/new', component: EmployeeFormComponent },
      { path: 'employees/:id', component: EmployeeFormComponent },
      { path: 'employees', component: EmployeeListComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
