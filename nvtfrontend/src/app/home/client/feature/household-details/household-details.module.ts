import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HouseholdDetailsRoutingModule } from './household-details-routing.module';
import { HouseholdDetailsFormModule } from '../../ui/household-details-form/household-details-form.module';


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    HouseholdDetailsRoutingModule,
    HouseholdDetailsFormModule
  ]
})
export class HouseholdDetailsModule { }
