import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HouseholdRequestDetailsFormRoutingModule } from './household-request-details-form-routing.module';
import { HouseholdRequestDetailsFormComponent } from './household-request-details-form.component';


@NgModule({
  declarations: [HouseholdRequestDetailsFormComponent],
  imports: [
    CommonModule,
    HouseholdRequestDetailsFormRoutingModule
  ],
  exports: [HouseholdRequestDetailsFormComponent]
})
export class HouseholdRequestDetailsFormModule { }
