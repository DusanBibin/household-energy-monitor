import { Component, OnInit } from '@angular/core';
import { ClientService } from '../../data-access/client.service';
import { ActivatedRoute } from '@angular/router';
import { HouseholdRequestDTO } from '../../data-access/model/client-model';
import { ResponseData, ResponseMessage } from '../../../../shared/model';
import { request } from 'http';
import { error } from 'console';
import { SnackBarService } from '../../../../shared/services/snackbar-service/snackbar.service';
@Component({
  selector: 'app-household-request-details',
  standalone: false,
  templateUrl: './household-request-details.component.html',
  styleUrl: './household-request-details.component.css'
})
export class HouseholdRequestDetailsComponent implements OnInit {
  realestateId!: number;
  householdId!: number;
  requestId!: number;
  requestDetails: ResponseData | null = null;
  pendingRequests: ResponseData | null = null;

  constructor(private clientService: ClientService, private route: ActivatedRoute, private snackBar: SnackBarService){
    
  }

  ngOnInit(): void {
     

      this.route.paramMap.subscribe(params => {
        this.realestateId = Number(params.get('realestateId'));
        this.householdId = Number(params.get('householdId'));
        this.requestId = Number(params.get('requestId'));
    
        // Fetch data based on new params
        this.fetchRequestDetails();
        
      });
  
  }

  private fetchRequestDetails(): void {
    this.clientService.getHouseholdRequestDetails(this.realestateId, this.householdId, this.requestId).subscribe({
      next: requestDetails => {
        this.requestDetails = { isError: false, data: requestDetails };
  
        let x = this.requestDetails.data as HouseholdRequestDTO
        if(x.requestStatus === "PENDING") this.getPendingRequests(0);
        
      },
      error: err => {
        this.requestDetails = { isError: true, error: err.error as ResponseMessage };
      }
    });
  }

  sendDecision(event:{isAccepted: boolean, reason: string | null} ){
    this.clientService.processHouseholdRequest(this.realestateId, this.householdId, this.requestId, event.isAccepted, event.reason).subscribe({
      next: requestDetails => {
   
        this.requestDetails = {isError: false, data: requestDetails}
        if(event.isAccepted) this.snackBar.openSnackBar("Successfully accepted household request")
        else this.snackBar.openSnackBar("Successfullly rejected household request")
      },error: err => {
        console.log(err)
        this.requestDetails = {isError: true, error: err.error as ResponseMessage}
      }
    })
  }


  getPendingRequests(page: number){
    this.clientService.getConflictedPendingRequests(this.realestateId, this.householdId, this.requestId, page, 10).subscribe({
      next: pendingRequests => {
        console.log(pendingRequests)
        this.pendingRequests = {isError: false, data: pendingRequests};
        console.log(this.requestDetails)

      },error: err => {
        this.pendingRequests = {isError: true, error: err.error as ResponseMessage}
      }
    })
  }



  handlePendingRequestsPaging(event: {page: number}){
    this.getPendingRequests(event.page)

  }

}
