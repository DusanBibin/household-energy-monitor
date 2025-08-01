import { AfterViewInit, Component, Input, OnChanges, ViewChild, SimpleChanges, EventEmitter, Output, TemplateRef } from '@angular/core';
import { CalendarOptions, EventInput, DatesSetArg } from '@fullcalendar/core/index.js';
import timeGridPlugin from '@fullcalendar/timegrid'
import { FullCalendarComponent } from '@fullcalendar/angular';
import enGbLocale from '@fullcalendar/core/locales/en-gb'
import { AppointmentDTO } from '../../data-access/model/client-model';
import { end } from '@popperjs/core';
import { ResponseData } from '../../../../shared/model';
import { PagedResponse } from '../household-requests-list/household-requests-list.component';
import { UserSummaryDTO } from '../../../../auth/data-access/model/auth-model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../../../environments/environment.development';

@Component({
  selector: 'app-client-appointments-dumb',
  standalone: false,
  templateUrl: './client-appointments-dumb.component.html',
  styleUrl: './client-appointments-dumb.component.css'
})
export class ClientAppointmentsDumbComponent implements OnChanges{

  envProfileImg = environment.apiUrl + "/file/profile-img/"


  @Input() appointments: AppointmentDTO[] = [];
  @Output() calendarViewChanged = new EventEmitter<{ start: Date, end: Date }>();


  @Output() pagingDetailsOutput = new EventEmitter<{page: number}>();
  @Input() clerksData: ResponseData | null = null;
  clerks: PagedResponse<UserSummaryDTO> | null = null;

  @ViewChild('clerksDialog') clerksDialog: TemplateRef<any> | null = null;
  isLoadingClerks = true;

  selectedId = 0;
  page = 0; 
  size = 10;
  totalPages = 0;

  @ViewChild('calendar') calendarComponent!: FullCalendarComponent;


  constructor(private modalService: NgbModal){}

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


    if(changes['clerksData']){
      if(this.clerksData){
        
        console.log("jel ima ovde staaa")
        console.log(this.clerksData)
        

        this.clerks = this.clerksData?.data as PagedResponse<UserSummaryDTO>;
        
        this.page = this.clerks.number
        this.totalPages = this.clerks.totalPages
        
        this.isLoadingClerks = false;
        console.log(this.isLoadingClerks)
      }
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






  resetAndDismiss(modal: any): void {
    modal.dismiss('Cancel click');
  }



  loadRequests(): void {
    this.isLoadingClerks = true
    this.clerksData = null;


    this.pagingDetailsOutput.emit({page: this.page})

  }




  goToPage(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.page = page;
      this.loadRequests();
    }
  }

  goPrevious() {
    if (this.page > 0) {
      this.page--;
      this.loadRequests();
    }
  }

  goNext() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadRequests();
    }
  }


  openClerkDialog(){
    if (this.clerksDialog) {
      
      this.modalService.open(this.clerksDialog, {
        centered: true,
        scrollable: true,
        backdrop: 'static',
      });
    }
  }

  navigateClerkAppointments(){

  }

}
