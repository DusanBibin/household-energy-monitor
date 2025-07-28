import { Component, OnInit, Input, SimpleChanges, ViewChild, TemplateRef, Output, EventEmitter } from '@angular/core';
import { HouseholdDetailsDTO, HouseholdRequestDTO } from '../../data-access/model/client-model';
import { environment } from '../../../../../environments/environment.development';
import { JwtService } from '../../../../shared/services/jwt-service/jwt.service';
import { Router } from '@angular/router';
import { ResponseData } from '../../../../shared/model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { SnackBarService } from '../../../../shared/services/snackbar-service/snackbar.service';

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
  isLoadingAccept = false;


  form = new FormGroup({
    reason: new FormControl('', Validators.required)
  });

  @Input() requestDetails: ResponseData | null = null;

  @Output() requestDecision = new EventEmitter<{isAccepted: boolean, reason: string | null}>();

  @ViewChild('denyDialog') denyDialog: TemplateRef<any> | null = null;
  constructor(protected jwtService: JwtService, private router: Router, private modalService: NgbModal, private snackBar: SnackBarService){}
  


  ngOnInit(): void {
      
  }

  ngOnChanges(changes: SimpleChanges){
      if(changes['requestDetails']){
        console.log("change se dogodio")
        console.log(this.requestDetails)
        
        if(this.requestDetails){
          this.isLoadingAccept = false;
          console.log("nije null request details")
        }
      }
  }

  navigate(){

    
    const role = this.jwtService.getRole()?.toLowerCase()
    
    if(this.requestDetails && !this.requestDetails.isError){ this.router.navigate(['/home', role, 'realestate', this.requestDetails.data.realestateId, 'household', this.requestDetails.data.householdId]); }
  }


  resetAndDismiss(modal: any): void {
    modal.dismiss('Cancel click');
    
  }

  denyRequest(modal: any){
    
    let reasonTemp = this.form.get('reason');
    if(reasonTemp){
      if (reasonTemp.invalid) {
        this.snackBar.openSnackBar("Reject reason is empty")
        return
      }
      this.isLoadingAccept = true;
      this.requestDecision.emit({isAccepted: false, reason: reasonTemp.value})
      this.resetAndDismiss(modal)
      this.form.get('reason')?.reset('');
    }
   
  }


  openDenyDialog(){
    if (this.denyDialog) {
      this.modalService.open(this.denyDialog, {
        centered: true,
        scrollable: true,
        backdrop: 'static',
      });
    }
  }


  acceptRequest(){
    this.isLoadingAccept = true;
    this.requestDecision.emit({isAccepted: true, reason: null})
  }

}
