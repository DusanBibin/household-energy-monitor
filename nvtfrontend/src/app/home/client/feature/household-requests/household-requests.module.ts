import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HouseholdRequestsRoutingModule } from './household-requests-routing.module';
import { HouseholdRequestsListModule } from '../../ui/household-requests-list/household-requests-list.module';
import { HouseholdRequestsComponent } from './household-requests.component';


@NgModule({
  declarations: [HouseholdRequestsComponent],
  imports: [
    CommonModule,
    HouseholdRequestsRoutingModule,
    HouseholdRequestsListModule
  ]
})
export class HouseholdRequestsModule { }
