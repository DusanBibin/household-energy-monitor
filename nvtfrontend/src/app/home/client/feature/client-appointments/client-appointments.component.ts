import { Component, OnInit } from '@angular/core';
import { ClientService } from '../../data-access/client.service';
import { AppointmentDTO } from '../../data-access/model/client-model';

@Component({
  selector: 'app-client-appointments',
  standalone: false,
  templateUrl: './client-appointments.component.html',
  styleUrl: './client-appointments.component.css'
})
export class ClientAppointmentsComponent implements OnInit{


  appointments: AppointmentDTO[] = [];

  constructor(private clientService: ClientService){}

  ngOnInit(): void {
    const today = new Date();
    const localDay = today.getDay(); // 0 = Sunday, 1 = Monday, ..., 6 = Saturday
    const diff = localDay === 0 ? -6 : 1 - localDay;
  
    // Get Monday of this week in local time
    const mondayLocal = new Date(today);
    mondayLocal.setDate(today.getDate() + diff);
    mondayLocal.setHours(0, 0, 0, 0);
  
    // Convert to UTC midnight
    const mondayThisWeek = new Date(Date.UTC(
      mondayLocal.getFullYear(),
      mondayLocal.getMonth(),
      mondayLocal.getDate(),
      0, 0, 0, 0
    ));
  
    // Get Monday of next week (7 days after)
    const mondayNextWeek = new Date(mondayThisWeek);
    mondayNextWeek.setUTCDate(mondayThisWeek.getUTCDate() + 7);
  
    this.clientService.getClientAppointments(mondayThisWeek, mondayNextWeek)
      .subscribe({
        next: (res) => {
          this.appointments = res;
          console.log("Fetched appointments successfully.");
        },
        error: (err) => console.error('Failed to fetch appointments:', err)
      });
  }



  onCalendarRangeChange(event: { start: Date, end: Date }): void {
    const start = new Date(Date.UTC(
      event.start.getFullYear(),
      event.start.getMonth(),
      event.start.getDate(),
      0, 0, 0, 0
    ));

    const end = new Date(Date.UTC(
      event.end.getFullYear(),
      event.end.getMonth(),
      event.end.getDate(),
      0, 0, 0, 0
    ));

    this.clientService.getClientAppointments(start, end)
      .subscribe({
        next: (res) => {
          this.appointments = res;
        
          console.log("Fetched appointments for updated calendar range.");
        },
        error: (err) => console.error('Failed to fetch appointments:', err)
      });
  }
  
}
