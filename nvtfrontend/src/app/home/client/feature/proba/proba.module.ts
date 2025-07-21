import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProbaRoutingModule } from './proba-routing.module';
import { ProbaComponent } from './proba.component';
import { ProbaFormModule } from '../../ui/proba-form/proba-form.module';


@NgModule({
  declarations: [ProbaComponent],
  imports: [
    CommonModule,
    ProbaRoutingModule,
    ProbaFormModule,
  ]
})
export class ProbaModule { }
