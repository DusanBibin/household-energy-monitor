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
  realestateId: number;
  householdId: number;
  requestId: number;
  requestDetails: ResponseData | null = null;

  constructor(private clientService: ClientService, private route: ActivatedRoute, private snackBar: SnackBarService){
    this.realestateId = Number(this.route.snapshot.paramMap.get('realestateId'));
    this.householdId = Number(this.route.snapshot.paramMap.get('householdId'));
    this.requestId = Number(this.route.snapshot.paramMap.get('requestId'));
  }

  ngOnInit(): void {
      this.clientService.getHouseholdRequestDetails(this.realestateId, this.householdId, this.requestId).subscribe({
        next: requestDetails => {
          this.requestDetails = {isError: false, data: requestDetails};
          console.log(this.requestDetails)

        },error: err => {
          this.requestDetails = {isError: true, error: err.error as ResponseMessage}
        }
      })
  
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

}
