import { AfterViewInit, Component, Input, OnChanges, ViewChild, SimpleChanges, EventEmitter, Output } from '@angular/core';
import { CalendarOptions, EventInput, DatesSetArg } from '@fullcalendar/core/index.js';
import timeGridPlugin from '@fullcalendar/timegrid'
import { FullCalendarComponent } from '@fullcalendar/angular';
import enGbLocale from '@fullcalendar/core/locales/en-gb'
import { AppointmentDTO } from '../../data-access/model/client-model';
import { end } from '@popperjs/core';

@Component({
  selector: 'app-client-appointments-dumb',
  standalone: false,
  templateUrl: './client-appointments-dumb.component.html',
  styleUrl: './client-appointments-dumb.component.css'
})
export class ClientAppointmentsDumbComponent implements OnChanges{

  @Input() appointments: AppointmentDTO[] = [];
  @Output() calendarViewChanged = new EventEmitter<{ start: Date, end: Date }>();

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
    slotMinTime: '08:00:00',
    slotMaxTime: '16:00:00',
    expandRows: true,
    datesSet: (arg: DatesSetArg) => {
      this.onCalendarViewChange(arg);
    }
  };

  ngAfterViewInit() {
    setTimeout(() => {
      const calendarApi = this.calendarComponent.getApi();
      calendarApi.updateSize();
    }, 20);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['appointments']) {
      this.updateCalendarEvents();
    }
  }

  private updateCalendarEvents(): void {
    const calendarApi = this.calendarComponent?.getApi();
    if (!calendarApi) return;

    calendarApi.removeAllEvents();
    console.log(this.appointments)
    const events = this.appointments.map(app => ({
      title: app.clerk.name + " " + app.clerk.lastname,
      start: app.startDateTime,
      end: app.endDateTime,
      allDay: false
    }));

    calendarApi.addEventSource(events);
  }

  onCalendarViewChange(arg: DatesSetArg): void {
    console.log('Calendar view changed:');
    console.log('Start date:', arg.start);
    console.log('End date:', arg.end);
    console.log('Current view:', arg.view.type);

    this.calendarViewChanged.emit({ start: arg.start, end: arg.end });
  }

}
