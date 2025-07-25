import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HouseholdRequestsListRoutingModule } from './household-requests-list-routing.module';
import { HouseholdRequestsListComponent } from './household-requests-list.component';
import { FormsModule } from '@angular/forms';


@NgModule({
  declarations: [HouseholdRequestsListComponent],
  imports: [
    CommonModule,
    HouseholdRequestsListRoutingModule,
    FormsModule
  ],
  exports: [HouseholdRequestsListComponent]
})
export class HouseholdRequestsListModule { }
