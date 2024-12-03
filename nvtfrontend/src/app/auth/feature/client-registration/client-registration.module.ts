import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClientRegistrationRoutingModule } from './client-registration-routing.module';
import { ClientRegistrationComponent } from './client-registration.component';


@NgModule({
  declarations: [ClientRegistrationComponent],
  imports: [
    CommonModule,
    ClientRegistrationRoutingModule
  ]
})
export class ClientRegistrationModule { }
