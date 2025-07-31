import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SchedulesRoutingModule } from './schedules-routing.module';
import { SchedulesComponent } from './schedules.component';
import { FullCalendarModule } from '@fullcalendar/angular';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import { SchedulesDumbModule } from "../../ui/schedules-dumb/schedules-dumb.module";




@NgModule({
  declarations: [SchedulesComponent],
  imports: [
    CommonModule,
    SchedulesRoutingModule,
    SchedulesDumbModule
]
})
export class SchedulesModule { }
