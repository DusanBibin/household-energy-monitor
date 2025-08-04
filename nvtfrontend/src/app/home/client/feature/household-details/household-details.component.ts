import { Component, OnInit } from '@angular/core';
import { HouseholdDetailsFormComponent } from "../../ui/household-details-form/household-details-form.component";
import { ActivatedRoute, Router } from '@angular/router';
import { ClientService } from '../../data-access/client.service';
import { HouseholdDetailsDTO } from '../../data-access/model/client-model';
import { FileService } from '../../../../shared/services/file-service/file.service';
import { switchMap, map, catchError, of } from 'rxjs';
import { SnackBarService } from '../../../../shared/services/snackbar-service/snackbar.service';
import { ResponseData, ResponseMessage } from '../../../../shared/model';
import { ConsumptionDTO } from '../../data-access/model/client-model';

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
              
  currentYM: { year: number, month: number } = this.getTodayYM();     
  chartData: ConsumptionDTO[] = [];


  constructor(private route: ActivatedRoute, private clientService: ClientService, private fileService: FileService, private snackService: SnackBarService){
    this.realestateId = Number(this.route.snapshot.paramMap.get('realestateId'));
    this.householdId = Number(this.route.snapshot.paramMap.get('householdId'));

  }

  ngOnInit(): void {
    this.loadData();
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
  

  loadData(): void {
    const { year, month } = this.currentYM;
    this.clientService.getMonthly(1, year, month).subscribe({
      next: values => {
        this.chartData = values;
      },error: err => {
        console.log(err);
      }
    })
  }

  nextMonth(): void {
    // move forward by 1 month
    if (this.currentYM.month === 12) {
      this.currentYM.year++;
      this.currentYM.month = 1;
    } else {
      this.currentYM.month++;
    }
    this.loadData();
  }

  prevMonth(): void {
    // move backward by 1 month
    if (this.currentYM.month === 1) {
      this.currentYM.year--;
      this.currentYM.month = 12;
    } else {
      this.currentYM.month--;
    }
    this.loadData();
  }

  

  private getTodayYM() {
    const d = new Date();
    return { year: d.getFullYear(), month: d.getMonth() + 1 };
  }

  handleMonthChange(event: {isForward:boolean}){
    if(event.isForward) this.nextMonth();
    else this.prevMonth();
  }



  handleToDailyChange(date: Date | null){
    if(date){
      const year = date.getFullYear();
      const month = date.getMonth() + 1; 
  
      this.clientService.getDaily(1, year, month).subscribe({
        next: values => {
          this.chartData = values;
        },error: err => {
          console.log(err)
        }
      })
    }else{
      this.loadData()
    }
   
  }


}
