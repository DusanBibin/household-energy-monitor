import { Component, OnInit } from '@angular/core';
import { ClientService } from '../../data-access/client.service';
import { ActivatedRoute } from '@angular/router';
import { HouseholdRequestDTO } from '../../data-access/model/client-model';
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
  requestDetails: HouseholdRequestDTO | null = null;

  constructor(private clientService: ClientService, private route: ActivatedRoute){
    this.realestateId = Number(this.route.snapshot.paramMap.get('realestateId'));
    this.householdId = Number(this.route.snapshot.paramMap.get('householdId'));
    this.requestId = Number(this.route.snapshot.paramMap.get('requestId'));
  }

  ngOnInit(): void {
      this.clientService.getHouseholdRequestDetails(this.realestateId, this.householdId, this.requestId).subscribe({
        next: requestDetails => {
          requestDetails = requestDetails
        },error: err => {
          console.log(err)
        }
      })
  
  }

}
