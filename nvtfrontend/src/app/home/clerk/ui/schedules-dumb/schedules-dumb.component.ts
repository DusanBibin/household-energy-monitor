import { AfterViewInit, Component, Input, OnChanges, ViewChild, SimpleChanges, EventEmitter, Output, TemplateRef } from '@angular/core';
import { CalendarOptions, EventInput, DatesSetArg } from '@fullcalendar/core/index.js';
import timeGridPlugin from '@fullcalendar/timegrid'
import { FullCalendarComponent, } from '@fullcalendar/angular';
import enGbLocale from '@fullcalendar/core/locales/en-gb'
import { AppointmentDTO } from '../../../client/data-access/model/client-model';
import { end } from '@popperjs/core';
import { PartialUserData, ResponseData } from '../../../../shared/model';
import { PagedResponse } from '../../../client/ui/household-requests-list/household-requests-list.component';
import { UserSummaryDTO } from '../../../../auth/data-access/model/auth-model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../../../environments/environment.development';
import { JwtService } from '../../../../shared/services/jwt-service/jwt.service';
import { Router } from '@angular/router';
import { title } from 'process';
import { EventClickArg } from '@fullcalendar/core';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-schedules-dumb',
  standalone: false,
  templateUrl: './schedules-dumb.component.html',
  styleUrl: './schedules-dumb.component.css'
})
export class SchedulesDumbComponent implements AfterViewInit{
  envProfileImg = environment.apiUrl + "/file/profile-img/";

  @Input() clerk: PartialUserData | null = null;
  @Input() appointments: AppointmentDTO[] = [];
  @Input() newAppointmentUpdate: AppointmentDTO | null = null;


  @Output() calendarViewChanged = new EventEmitter<{ start: Date, end: Date }>();
  @Output() appointmentDataEmmitter = new EventEmitter<{ clerkId: number, startDate: string }>();

  @ViewChild('appointmentDialog') appointmentDialog: TemplateRef<any> | null = null;
  @ViewChild('calendar') calendarComponent!: FullCalendarComponent;

  selectedDate: { startDate: Date, endDate: Date } | null = null;

  isLoadingCalendar: boolean = false;

  constructor(
    private modalService: NgbModal,
    protected jwtService: JwtService,
    private router: Router,
    private datePipe: DatePipe
  ) {}

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
    },
    eventClick: this.onCalendarSlotClick.bind(this)
  };

  ngAfterViewInit() {
    setTimeout(() => {
      const calendarApi = this.calendarComponent.getApi();
      calendarApi.updateSize();
    }, 5000);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (
      changes['appointments'] &&
      this.appointments &&
      this.clerk &&
      this.calendarComponent
    ) {
      this.updateCalendarEvents();
      console.log(this.appointments)
      this.isLoadingCalendar = false;
    }


    if(changes['newAppointmentUpdate'] && this.newAppointmentUpdate){
      console.log(this.newAppointmentUpdate)  
      const calendarApi = this.calendarComponent?.getApi();
      if (!calendarApi) return;

      const { startDateTime, endDateTime } = this.newAppointmentUpdate;

      // Remove overlapping blue slots
      calendarApi.getEvents().forEach(event => {
        if (
          event.title === 'Available' &&
          event.start &&
          event.end &&
          new Date(startDateTime) < event.end &&
          new Date(endDateTime) > event.start
        ) {
          event.remove();
        }
      });

      // Add new red event
      calendarApi.addEvent({
        title: 'Not available',
        start: startDateTime,
        end: endDateTime,
        color: 'red',
        allDay: false
      });
      }

  }

  private updateCalendarEvents(): void {
    const calendarApi = this.calendarComponent?.getApi();
    if (!calendarApi || !this.clerk) return;

    calendarApi.removeAllEvents();

    const calendarView = calendarApi.view;
    const viewStart = new Date(calendarView.currentStart);
    const viewEnd = new Date(calendarView.currentEnd);

    const appointmentEvents = this.appointments.map(app => ({
      title: 'Not available',
      start: app.startDateTime,
      end: app.endDateTime,
      color: 'red',
      allDay: false
    }));

    const blueSlots = this.generateBlueSlots(viewStart, viewEnd, this.appointments);
    const allEvents = [...blueSlots, ...appointmentEvents];

    calendarApi.addEventSource(allEvents);
  }

  private generateBlueSlots(viewStart: Date, viewEnd: Date, appointments: AppointmentDTO[]): any[] {
    const slots: any[] = [];
    const now = new Date();
    const startHour = 8;
    const endHour = 16;
    const lunchStart = 12;
    const lunchEnd = 12.5;

    const current = new Date(viewStart);
    current.setHours(0, 0, 0, 0);

    while (current < viewEnd) {
      const day = current.getDay();
      if (day >= 1 && day <= 5) {
        for (let time = startHour; time < endHour; time += 0.5) {
          if (time >= lunchStart && time < lunchEnd) continue;

          const slotStart = new Date(current);
          slotStart.setHours(Math.floor(time), (time % 1) * 60, 0, 0);
          const slotEnd = new Date(slotStart);
          slotEnd.setMinutes(slotStart.getMinutes() + 30);

          const hoursDifference = (slotStart.getTime() - now.getTime()) / (1000 * 60 * 60);
          if (slotEnd <= now || hoursDifference < 24) continue;
          // if (slotEnd <= now) continue;

          const overlaps = appointments.some(app => {
            const appStart = new Date(app.startDateTime);
            const appEnd = new Date(app.endDateTime);
            return slotStart < appEnd && slotEnd > appStart;
          });

          if (!overlaps) {
            slots.push({
              title: 'Available',
              start: slotStart.toISOString(),
              end: slotEnd.toISOString(),
              color: '#007bff',
              allDay: false
            });
          }
        }
      }
      current.setDate(current.getDate() + 1);
    }

    return slots;
  }

  onCalendarSlotClick(arg: EventClickArg): void {
    const event = arg.event;

    if (event.title === 'Available') {
      const start = event.start;
      const end = event.end;

      if (start && end) {
        this.selectedDate = { startDate: start, endDate: end };
      }

      if (this.appointmentDialog) {
        this.modalService.open(this.appointmentDialog, {
          centered: true,
          scrollable: true,
          backdrop: 'static',
        });
      }
    }
  }

  onCalendarViewChange(arg: DatesSetArg): void {
    this.isLoadingCalendar = true;
    this.calendarViewChanged.emit({ start: arg.start, end: arg.end });
  }

  createAppointment(modal: any) {
    modal.dismiss('Cancel click');

    if (this.clerk && this.selectedDate) {
      const formattedDate = this.datePipe.transform(this.selectedDate.startDate, 'dd/MM/yyyy-HH:mm');
      if (formattedDate) {
        this.appointmentDataEmmitter.emit({ clerkId: this.clerk.id, startDate: formattedDate });
      }
    }
  }


}
