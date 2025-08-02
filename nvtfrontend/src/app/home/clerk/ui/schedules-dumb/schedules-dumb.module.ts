import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

import { SchedulesDumbRoutingModule } from './schedules-dumb-routing.module';
import { SchedulesDumbComponent } from './schedules-dumb.component';
import { FullCalendarModule } from '@fullcalendar/angular';


@NgModule({
  declarations: [SchedulesDumbComponent],
  imports: [
    CommonModule,
    SchedulesDumbRoutingModule,
    FullCalendarModule
  ],
  exports:[SchedulesDumbComponent],
  providers: [DatePipe]
})
export class SchedulesDumbModule { }
