import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { VacantHouseholdsClientRoutingModule } from './vacant-households-client-routing.module';
import { VacantHouseholdsClientComponent } from './vacant-households-client.component';


@NgModule({
  declarations: [VacantHouseholdsClientComponent],
  imports: [
    CommonModule,
    VacantHouseholdsClientRoutingModule
  ]
})
export class VacantHouseholdsClientModule { }
