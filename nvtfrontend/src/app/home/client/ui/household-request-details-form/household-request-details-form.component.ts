import { Component, OnInit, Input, SimpleChanges } from '@angular/core';
import { HouseholdDetailsDTO } from '../../data-access/model/client-model';

@Component({
  selector: 'app-household-request-details-form',
  standalone: false,
  templateUrl: './household-request-details-form.component.html',
  styleUrl: './household-request-details-form.component.css'
})
export class HouseholdRequestDetailsFormComponent implements OnInit {
  
  isLoading = true;
  @Input() requestDetails: HouseholdDetailsDTO | null = null;

  
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
