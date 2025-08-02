import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClientAppointmentsDumbRoutingModule } from './client-appointments-dumb-routing.module';
import { ClientAppointmentsDumbComponent } from './client-appointments-dumb.component';
import { FullCalendarModule } from '@fullcalendar/angular';


@NgModule({
  declarations: [ClientAppointmentsDumbComponent],
  imports: [
    CommonModule,
    ClientAppointmentsDumbRoutingModule,
    FullCalendarModule
  ],
  exports: [ClientAppointmentsDumbComponent]
})
export class ClientAppointmentsDumbModule { }
