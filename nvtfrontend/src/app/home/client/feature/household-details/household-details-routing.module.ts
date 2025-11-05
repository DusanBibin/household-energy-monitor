import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HouseholdDetailsComponent } from './household-details.component';

const routes: Routes = [
  {
    path: '',
    component: HouseholdDetailsComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HouseholdDetailsRoutingModule { }
