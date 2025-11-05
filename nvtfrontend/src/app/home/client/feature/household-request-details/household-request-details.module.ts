import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HouseholdRequestDetailsRoutingModule } from './household-request-details-routing.module';
import { HouseholdRequestDetailsFormModule } from '../../ui/household-request-details-form/household-request-details-form.module';
import { HouseholdRequestDetailsComponent } from './household-request-details.component';


@NgModule({
  declarations: [HouseholdRequestDetailsComponent],
  imports: [
    CommonModule,
    HouseholdRequestDetailsRoutingModule,
    HouseholdRequestDetailsFormModule
  ]
})
export class HouseholdRequestDetailsModule { }
