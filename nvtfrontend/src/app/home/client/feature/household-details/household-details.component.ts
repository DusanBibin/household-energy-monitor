import { Component, OnInit } from '@angular/core';
import { HouseholdDetailsFormComponent } from "../../ui/household-details-form/household-details-form.component";
import { ActivatedRoute, Router } from '@angular/router';
import { ClientService } from '../../data-access/client.service';
import { HouseholdDetailsDTO } from '../../data-access/model/client-model';
import { FileService } from '../../../../shared/services/file-service/file.service';
import { switchMap, map, catchError, of } from 'rxjs';

@Component({
  selector: 'app-household-details',
  standalone: false,
  templateUrl: './household-details.component.html',
  styleUrl: './household-details.component.css'
})
export class HouseholdDetailsComponent implements OnInit {



  householdDetails: HouseholdDetailsDTO | null = null;

  constructor(private route: ActivatedRoute, private clientService: ClientService, private fileService: FileService){

  }

  ngOnInit(): void {
    
    console.log(this.route.snapshot.paramMap)
    const realestateId = Number(this.route.snapshot.paramMap.get('realestateId'));
    const householdId = Number(this.route.snapshot.paramMap.get('householdId'));


    this.clientService.getHouseholdDetails(realestateId, householdId).pipe(
      switchMap(householdDetails => {
        
    
        if (householdDetails.user?.id) {
          return this.fileService.getProfileImageParam(householdDetails.user.id).pipe(
            map(profileImg => {
              const imgUri = URL.createObjectURL(profileImg);
              householdDetails.user.profileImg = imgUri;
              console.log(householdDetails)
              this.householdDetails = householdDetails;
              return householdDetails;
            }),
            catchError(err => {
              console.error('Error loading profile image:', err);
              return of(householdDetails); 
            })
          );
        }else this.householdDetails = householdDetails;
    
        return of(householdDetails); 
      })
    ).subscribe({
      next: updatedDetails => {
        this.householdDetails = updatedDetails;
      },
      error: err => {
        console.error('Error fetching household details:', err);
      }
    });


    console.log(realestateId)
    console.log(householdId )
  }
}
