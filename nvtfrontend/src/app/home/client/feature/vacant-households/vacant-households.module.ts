import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { VacantHouseholdsRoutingModule } from './vacant-households-routing.module';
import { VacantHouseholdsComponent } from './vacant-households.component';


@NgModule({
  declarations: [VacantHouseholdsComponent],
  imports: [
    CommonModule,
    VacantHouseholdsRoutingModule
  ]
})
export class VacantHouseholdsModule { }
