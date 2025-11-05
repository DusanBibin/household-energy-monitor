import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SuperadminChangePasswordRoutingModule } from './superadmin-change-password-routing.module';
import { SuperadminChangePasswordFormModule } from '../../ui/superadmin-change-password-form/superadmin-change-password-form.module';
import { SuperadminChangePasswordComponent } from './superadmin-change-password.component';


@NgModule({
  declarations: [SuperadminChangePasswordComponent],
  imports: [
    CommonModule,
    SuperadminChangePasswordRoutingModule,
    SuperadminChangePasswordFormModule
  ]
})
export class SuperadminChangePasswordModule { }
