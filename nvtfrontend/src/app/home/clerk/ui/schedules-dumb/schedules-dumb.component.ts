import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { CalendarOptions, EventInput, DatesSetArg } from '@fullcalendar/core/index.js';
import timeGridPlugin from '@fullcalendar/timegrid'
import { FullCalendarComponent } from '@fullcalendar/angular';
import enGbLocale from '@fullcalendar/core/locales/en-gb'

@Component({
  selector: 'app-schedules-dumb',
  standalone: false,
  templateUrl: './schedules-dumb.component.html',
  styleUrl: './schedules-dumb.component.css'
})
export class SchedulesDumbComponent implements AfterViewInit{

  @ViewChild('calendar') calendarComponent!: FullCalendarComponent;

  calendarOptions: CalendarOptions = {
    plugins: [timeGridPlugin],
    initialView: 'timeGridWeek',
    headerToolbar: {
      left: 'prev,next',
      center: 'title'
    },
    locale: enGbLocale,
    dayHeaderFormat: {
      weekday: 'short',
      day: 'numeric',
      month: 'numeric',
    },
    allDaySlot: false,
    slotDuration: '00:30:00',
    slotLabelInterval: '01:00:00',
    events: (fetchInfo, successCallback, failureCallback) => {
      const events = this.generateTimeSlots(fetchInfo.start, fetchInfo.end);
      successCallback(events);
    },
    datesSet: (arg: DatesSetArg) => {
      const calendarApi = this.calendarComponent.getApi();
      calendarApi.refetchEvents(); // refresh slots whenever week changes
    }
  };

  ngAfterViewInit() {
    setTimeout(() => {
      const calendarApi = this.calendarComponent.getApi();
      calendarApi.updateSize();
    }, 20);
  }

  private generateTimeSlots(startDate: Date, endDate: Date): EventInput[] {
    const slots: EventInput[] = [];

    const startHour = 8;
    const endHour = 16;
    const excludedStart = 12;
    const excludedEnd = 12.5;

    const current = new Date(startDate);
    current.setHours(0, 0, 0, 0);

    while (current < endDate) {
      const day = current.getDay(); 

      if (day >= 1 && day <= 5) { 
        for (let time = startHour; time < endHour; time += 0.5) {
          if (time >= excludedStart && time < excludedEnd) continue;

          const slotStart = new Date(current);
          const hour = Math.floor(time);
          const minute = (time % 1) * 60;
          slotStart.setHours(hour, minute, 0, 0);

          const slotEnd = new Date(slotStart);
          slotEnd.setMinutes(slotStart.getMinutes() + 30);

          slots.push({
            title: 'Available Slot',
            start: slotStart.toISOString(),
            end: slotEnd.toISOString(),
            allDay: false
          });
        }
      }

      current.setDate(current.getDate() + 1);
    }

    return slots;
  }
 
}
