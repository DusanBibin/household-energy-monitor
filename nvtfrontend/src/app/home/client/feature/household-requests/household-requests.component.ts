import { Component } from '@angular/core';
import { ClientService } from '../../data-access/client.service';
import { PagedResponse, PagingDetails } from '../../ui/household-requests-list/household-requests-list.component';
import { HouseholdRequestPreviewDTO } from '../../data-access/model/client-model';
@Component({
  selector: 'app-household-requests',
  standalone: false,
  templateUrl: './household-requests.component.html',
  styleUrl: './household-requests.component.css'
})
export class HouseholdRequestsComponent {
  constructor(private clientService: ClientService){}





  pagingDetailsInput:  PagedResponse<HouseholdRequestPreviewDTO> | null = null;

  handlePagingDetails(details: PagingDetails){
    console.log(details)

    this.clientService.getClientRequests(details.status, details.page, 10, details.sortField, details.sortDir).subscribe({
      next: (response: PagedResponse<HouseholdRequestPreviewDTO>) => {
        this.pagingDetailsInput = response

      }, error: error => {
        console.log("neka greskaaa")
        console.log(error)
      }
    })
  }


}
