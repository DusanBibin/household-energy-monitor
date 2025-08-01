import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClientAppointmentsDumbRoutingModule } from './client-appointments-dumb-routing.module';
import { ClientAppointmentsDumbComponent } from './client-appointments-dumb.component';


@NgModule({
  declarations: [ClientAppointmentsDumbComponent],
  imports: [
    CommonModule,
    ClientAppointmentsDumbRoutingModule
  ],
  exports: [ClientAppointmentsDumbComponent]
})
export class ClientAppointmentsDumbModule { }
