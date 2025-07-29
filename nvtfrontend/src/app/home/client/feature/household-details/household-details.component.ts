import { Component, OnInit } from '@angular/core';
import { HouseholdDetailsFormComponent } from "../../ui/household-details-form/household-details-form.component";
import { ActivatedRoute, Router } from '@angular/router';
import { ClientService } from '../../data-access/client.service';
import { HouseholdDetailsDTO } from '../../data-access/model/client-model';
import { FileService } from '../../../../shared/services/file-service/file.service';
import { switchMap, map, catchError, of } from 'rxjs';
import { SnackBarService } from '../../../../shared/services/snackbar-service/snackbar.service';
import { ResponseData, ResponseMessage } from '../../../../shared/model';

@Component({
  selector: 'app-household-details',
  standalone: false,
  templateUrl: './household-details.component.html',
  styleUrl: './household-details.component.css'
})
export class HouseholdDetailsComponent implements OnInit {



  householdDetails: ResponseData | null = null;
  realestateId: number;
  householdId: number;




  constructor(private route: ActivatedRoute, private clientService: ClientService, private fileService: FileService, private snackService: SnackBarService){
    this.realestateId = Number(this.route.snapshot.paramMap.get('realestateId'));
    this.householdId = Number(this.route.snapshot.paramMap.get('householdId'));

  }

  ngOnInit(): void {
    
    console.log(this.route.snapshot.paramMap)


    this.clientService.getHouseholdDetails(this.realestateId, this.householdId).subscribe({
      next: householdDetails => {
        this.householdDetails = { isError: false, data: householdDetails };
        console.log('Household details:', householdDetails);
      },
      error: err => {
        this.householdDetails = { isError: true, error: err.error as ResponseMessage };
        console.error('Error fetching household details:', err);
      }
    });


    console.log(this.realestateId)
    console.log(this.householdId )
  }


  createClaimRequest(files: { id: number, file: File | null }[]) {
    const MAX_SIZE_BYTES = 1 * 1024 * 1024 * 1024; // 1 GB 
  
    const filesOnly: File[] = files
      .filter(item => item.file !== null)
      .map(item => item.file as File);
  
 
    const hasTooBigFile = filesOnly.some(file => file.size > MAX_SIZE_BYTES);
  
    if (hasTooBigFile) {
      console.log('One or more files exceed the 1 GB size limit');
      this.snackService.openSnackBar("One or more files exceed the 1 GB size limit");
      return; 
    } else {
      console.log('All files are within size limits');
    }
  
    console.log(filesOnly);
    console.log(this.realestateId);
    console.log(this.householdId);
  
    this.clientService.createHouseholdClaim(this.realestateId, this.householdId, filesOnly).subscribe({
      next: (newDetails) => {
        this.householdDetails = { isError: false, data: newDetails };
        this.snackService.openSnackBar("Successfully created new request");
      },
      error: err => {
        console.log(err);
        if (err.status === 413) {
          this.snackService.openSnackBar("The files for the request are too large");
        }
      }
    });
  }
  


}
