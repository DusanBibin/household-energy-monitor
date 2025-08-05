import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HouseholdDetailsFormRoutingModule } from './household-details-form-routing.module';
import { GoogleMapsModule } from '@angular/google-maps';
import { HouseholdDetailsFormComponent } from './household-details-form.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxEchartsModule, provideEchartsCore } from 'ngx-echarts';

@NgModule({
  declarations: [HouseholdDetailsFormComponent],
  imports: [
    CommonModule,
    HouseholdDetailsFormRoutingModule,
    ReactiveFormsModule,
    GoogleMapsModule,
    NgxEchartsModule,
    FormsModule
  ],
  exports: [HouseholdDetailsFormComponent],
  providers: [
    provideEchartsCore({
      echarts: () => import('echarts')
    })
  ]
})
export class HouseholdDetailsFormModule {
  
 }
