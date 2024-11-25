import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SuperadminChangePasswordFormRoutingModule } from './superadmin-change-password-form-routing.module';
import { SuperadminChangePasswordComponent } from '../../feature/superadmin-change-password/superadmin-change-password.component';
import { SuperadminChangePasswordFormComponent } from './superadmin-change-password-form.component';


@NgModule({
  declarations: [SuperadminChangePasswordFormComponent],
  imports: [
    CommonModule,
    SuperadminChangePasswordFormRoutingModule
  ],
  exports:[SuperadminChangePasswordFormComponent]
})
export class SuperadminChangePasswordFormModule { }
