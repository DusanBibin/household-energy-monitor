import { Component } from '@angular/core';
import { PagingDetails } from '../household-requests-list/household-requests-list.component';
import { AppointmentDTO } from '../../data-access/model/client-model';


@Component({
  selector: 'app-client-appointments-dumb',
  standalone: false,
  templateUrl: './client-appointments-dumb.component.html',
  styleUrl: './client-appointments-dumb.component.css'
})
export class ClientAppointmentsDumbComponent {
    appointments: AppointmentDTO[] = [];
    page = 0; 
    size = 10;
    totalPages = 0;
   
    isLoading = true;
  
    selectedId = 0;

    loadRequests(): void {
      this.isLoading = true;
      this.appointments = [];



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



}
