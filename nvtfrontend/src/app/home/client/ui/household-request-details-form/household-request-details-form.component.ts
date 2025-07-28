import { Component, OnInit, Input, SimpleChanges } from '@angular/core';
import { HouseholdDetailsDTO, HouseholdRequestDTO } from '../../data-access/model/client-model';
import { environment } from '../../../../../environments/environment.development';
import { JwtService } from '../../../../shared/services/jwt-service/jwt.service';
import { Router } from '@angular/router';
import { ResponseData } from '../../../../shared/model';
@Component({
  selector: 'app-household-request-details-form',
  standalone: false,
  templateUrl: './household-request-details-form.component.html',
  styleUrl: './household-request-details-form.component.css'
})
export class HouseholdRequestDetailsFormComponent implements OnInit {
  
  envProfileImg = environment.apiUrl + "/file/profile-img/"
  envRequestFile = environment.apiUrl + "/file/household-request/"
  isLoading = true;
  @Input() requestDetails: ResponseData | null = null;

  
  constructor(protected jwtService: JwtService, private router: Router){

  }
  
  ngOnInit(): void {
      
  }

  ngOnChanges(changes: SimpleChanges){
      if(changes['requestDetails']){
        console.log("change se dogodio")
        console.log(this.requestDetails)
        
        if(this.requestDetails){
          console.log("nije null request details")
        }
      }
  }

  navigate(){

    
    const role = this.jwtService.getRole()?.toLowerCase()
    
    if(this.requestDetails && !this.requestDetails.isError){ this.router.navigate(['/home', role, 'realestate', this.requestDetails.data.realestateId, 'household', this.requestDetails.data.householdId]); }
  }
}
