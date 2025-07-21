import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProbaFormRoutingModule } from './proba-form-routing.module';
import { ProbaFormComponent } from './proba-form.component';


@NgModule({
  declarations: [ProbaFormComponent],
  imports: [
    CommonModule,
    ProbaFormRoutingModule
  ],
  exports: [ProbaFormComponent]
})
export class ProbaFormModule { }
