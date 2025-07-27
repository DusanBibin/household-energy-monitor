import { Component, OnInit, Input, SimpleChanges } from '@angular/core';
import { HouseholdDetailsDTO, HouseholdRequestDTO } from '../../data-access/model/client-model';
import { environment } from '../../../../../environments/environment.development';
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
  @Input() requestDetails: HouseholdRequestDTO | null = null;

  
  constructor(){

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
}
