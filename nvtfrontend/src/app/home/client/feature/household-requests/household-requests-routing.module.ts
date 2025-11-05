import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HouseholdRequestsComponent } from './household-requests.component';

const routes: Routes = [
  {
    path: '',
    component: HouseholdRequestsComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HouseholdRequestsRoutingModule { }
