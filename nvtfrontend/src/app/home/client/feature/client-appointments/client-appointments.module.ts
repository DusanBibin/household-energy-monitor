import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClientAppointmentsRoutingModule } from './client-appointments-routing.module';
import { ClientAppointmentsComponent } from './client-appointments.component';
import { ClientAppointmentsDumbModule } from '../../ui/client-appointments-dumb/client-appointments-dumb.module';


@NgModule({
  declarations: [ClientAppointmentsComponent],
  imports: [
    CommonModule,
    ClientAppointmentsRoutingModule,
    ClientAppointmentsDumbModule
  ]
})
export class ClientAppointmentsModule { }
