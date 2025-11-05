import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HouseholdRequestDetailsFormRoutingModule } from './household-request-details-form-routing.module';
import { HouseholdRequestDetailsFormComponent } from './household-request-details-form.component';
import { ReactiveFormsModule } from '@angular/forms';


@NgModule({
  declarations: [HouseholdRequestDetailsFormComponent],
  imports: [
    CommonModule,
    HouseholdRequestDetailsFormRoutingModule,
    ReactiveFormsModule
  ],
  exports: [HouseholdRequestDetailsFormComponent]
})
export class HouseholdRequestDetailsFormModule { }
