import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { VerificationDumbRoutingModule } from './verification-dumb-routing.module';
import { VerificationDumbComponent } from './verification-dumb.component';


@NgModule({
  declarations: [
    VerificationDumbComponent
  ],
  imports: [
    CommonModule,
    VerificationDumbRoutingModule
  ],
  exports: [VerificationDumbComponent]
})
export class VerificationDumbModule { }
