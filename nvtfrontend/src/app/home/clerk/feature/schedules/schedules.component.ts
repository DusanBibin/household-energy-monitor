import { Component, OnInit } from '@angular/core';
import { ClientService } from '../../../client/data-access/client.service';
import { AppointmentDTO } from '../../../client/data-access/model/client-model';
import { PartialUserData, ResponseData } from '../../../../shared/model';
import { JwtService } from '../../../../shared/services/jwt-service/jwt.service';
import { ActivatedRoute, Route } from '@angular/router';
import { UserService } from '../../../../shared/services/user-service/user.service';
import { app } from '../../../../../../server';
import { SnackBarService } from '../../../../shared/services/snackbar-service/snackbar.service';

@Component({
  selector: 'app-schedules',
  standalone: false,
  templateUrl: './schedules.component.html',
  styleUrl: './schedules.component.css'
})
export class SchedulesComponent {
  
  clerkId: number = 0;
  clerksData: ResponseData | null = null;
  appointments: AppointmentDTO[] = [];
  clerk: PartialUserData | null = null;

  constructor(private clientService: ClientService, private jwtService: JwtService, private route: ActivatedRoute, 
    private userService: UserService, private snackBar: SnackBarService){}

  ngOnInit(): void {


    const clerkId = Number(this.route.snapshot.paramMap.get('clerkId'));
    console.log("izes mi kurac")
    console.log(clerkId)
    this.clerkId = clerkId;

    this.userService.getUserData(clerkId).subscribe({
      next: userData => {
        this.clerk = userData;
      }, error: err=> {
        console.log(err)
      }
    })

    // const today = new Date();
    // const localDay = today.getDay(); // 0 = Sunday, 1 = Monday, ..., 6 = Saturday
    // const diff = localDay === 0 ? -6 : 1 - localDay;
  
    // // Get Monday of this week in local time
    // const mondayLocal = new Date(today);
    // mondayLocal.setDate(today.getDate() + diff);
    // mondayLocal.setHours(0, 0, 0, 0);
  
    // // Convert to UTC midnight
    // const mondayThisWeek = new Date(Date.UTC(
    //   mondayLocal.getFullYear(),
    //   mondayLocal.getMonth(),
    //   mondayLocal.getDate(),
    //   0, 0, 0, 0
    // ));
  
    // // Get Monday of next week (7 days after)
    // const mondayNextWeek = new Date(mondayThisWeek);
    // mondayNextWeek.setUTCDate(mondayThisWeek.getUTCDate() + 7);


    // this.clientService.getClerkAppointments(clerkId, mondayThisWeek, mondayNextWeek).subscribe({
    //   next: appointmentsValue => {
    //     this.appointments = appointmentsValue;
    //   },error: err => {
    //     console.log(err)
    //   }
    // })



  




    
  
    // this.clientService.getClientAppointments(mondayThisWeek, mondayNextWeek)
    //   .subscribe({
    //     next: (res) => {
    //       this.appointments = res;
    //       console.log("Fetched appointments successfully.");
    //     },
    //     error: (err) => console.error('Failed to fetch appointments:', err)
    //   });
    //   if(this.jwtService.hasRole(['CLIENT'])) this.getClerks(0);
    // }

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

      
    console.log(start)
    console.log(end)
    this.clientService.getClerkAppointments(this.clerkId, start, end).subscribe({
      next: appointmentsValue => {
        this.appointments = appointmentsValue;
      },error: err => {
        console.log(err)
      }
    })
    


  }



  handleAppointmentCreate(event: {clerkId: number, startDate: string}){

    console.log("IZES MI KURAC VISE JEBEM TI MAMU")
    console.log(event.startDate)
    this.clientService.createAppointment(event.clerkId, event.startDate).subscribe({
      next: appointment => {
        console.log(appointment)
        this.snackBar.openSnackBar("Appointment successfully created")
      },error: err => {
        console.log(err)
      }
    })


  }
 


  
}
