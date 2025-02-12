import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VacantHouseholdsRoutingModule } from './vacant-households-routing.module';
import { VacantHouseholdsComponent } from './vacant-households.component';
import { ReactiveFormsModule } from '@angular/forms'; 
import { VacantHouseholdsFormModule } from '../../ui/vacant-households-form/vacant-households-form.module';

@NgModule({
  declarations: [VacantHouseholdsComponent],
  imports: [
    CommonModule,
    VacantHouseholdsRoutingModule,
    ReactiveFormsModule,
    VacantHouseholdsFormModule
  ]
})
export class VacantHouseholdsModule { }
