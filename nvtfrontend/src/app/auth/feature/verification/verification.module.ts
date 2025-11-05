import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { VerificationRoutingModule } from './verification-routing.module';
import { VerificationComponent } from './verification.component';
import { VerificationDumbModule } from '../../ui/verification-dumb/verification-dumb.module';


@NgModule({
  declarations: [
    VerificationComponent
  ],
  imports: [
    CommonModule,
    VerificationRoutingModule,
    VerificationDumbModule
  ]
})
export class VerificationModule { }
