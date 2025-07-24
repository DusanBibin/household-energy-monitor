import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HouseholdRequestsListRoutingModule } from './household-requests-list-routing.module';
import { HouseholdRequestsListComponent } from './household-requests-list.component';


@NgModule({
  declarations: [HouseholdRequestsListComponent],
  imports: [
    CommonModule,
    HouseholdRequestsListRoutingModule
  ],
  exports: [HouseholdRequestsListComponent]
})
export class HouseholdRequestsListModule { }
