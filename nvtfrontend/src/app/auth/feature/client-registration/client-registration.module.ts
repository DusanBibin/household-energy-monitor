import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClientRegistrationRoutingModule } from './client-registration-routing.module';
import { ClientRegistrationComponent } from './client-registration.component';
import { ClientRegistrationFormModule } from '../../ui/client-registration-form/client-registration-form.module';


@NgModule({
  declarations: [ClientRegistrationComponent],
  imports: [
    CommonModule,
    ClientRegistrationRoutingModule,
    ClientRegistrationFormModule
]
})
export class ClientRegistrationModule { }
