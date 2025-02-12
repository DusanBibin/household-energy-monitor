import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { VacantHouseholdsFormRoutingModule } from './vacant-households-form-routing.module';
import { VacantHouseholdsFormComponent } from './vacant-households-form.component';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [VacantHouseholdsFormComponent],
  imports: [
    CommonModule,
    VacantHouseholdsFormRoutingModule,
    ReactiveFormsModule
  ],
  exports: [VacantHouseholdsFormComponent]
})
export class VacantHouseholdsFormModule { }
