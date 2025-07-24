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
  realestateId: number;
  householdId: number;




  constructor(private route: ActivatedRoute, private clientService: ClientService, private fileService: FileService){
    this.realestateId = Number(this.route.snapshot.paramMap.get('realestateId'));
    this.householdId = Number(this.route.snapshot.paramMap.get('householdId'));

  }

  ngOnInit(): void {
    
    console.log(this.route.snapshot.paramMap)


    this.clientService.getHouseholdDetails(this.realestateId, this.householdId).pipe(
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


    console.log(this.realestateId)
    console.log(this.householdId )
  }


  createClaimRequest(files: { id: number, file: File | null }[]){

    const filesOnly: File[] = files.filter(item => item.file !== null).map(item => item.file as File)


    console.log(filesOnly)
    console.log(this.realestateId)
    console.log(this.householdId);

    this.clientService.createHouseholdClaim(this.realestateId, this.householdId, filesOnly).subscribe({
      next: () => {
        console.log("I NEED DA SUCCESS")
      },error: err => {
        console.log(err)

      }
    })


  }


}
