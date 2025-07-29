import { Component, OnInit, Input, SimpleChanges, ViewChild, TemplateRef, Output, EventEmitter } from '@angular/core';
import { HouseholdDetailsDTO, HouseholdRequestDTO, HouseholdRequestPreviewDTO } from '../../data-access/model/client-model';
import { environment } from '../../../../../environments/environment.development';
import { JwtService } from '../../../../shared/services/jwt-service/jwt.service';
import { Router } from '@angular/router';
import { ResponseData } from '../../../../shared/model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { SnackBarService } from '../../../../shared/services/snackbar-service/snackbar.service';
import { PagedResponse } from '../household-requests-list/household-requests-list.component';

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
  selectedId = 0;
  isLoadingPending = true;
  isLoadingDecision = false;


  page = 0; 
  size = 10;
  totalPages = 0;

  form = new FormGroup({
    reason: new FormControl('', Validators.required)
  });

  @Input() requestDetails: ResponseData | null = null;
  @Output() requestDecision = new EventEmitter<{isAccepted: boolean, reason: string | null}>();

  @Input() pendingRequests: ResponseData | null = null;
  helperPendingRequests: PagedResponse<HouseholdRequestPreviewDTO> | null = null;
  @Output() pagingDetailsOutput = new EventEmitter<{page: number}>();

  @ViewChild('acceptDialog') acceptDialog: TemplateRef<any> | null = null;
  @ViewChild('denyDialog') denyDialog: TemplateRef<any> | null = null;
  constructor(protected jwtService: JwtService, private router: Router, private modalService: NgbModal, private snackBar: SnackBarService){}
  


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

      if(changes['pendingRequests']){
        if(this.pendingRequests){
          


          

          this.helperPendingRequests = this.pendingRequests?.data as PagedResponse<HouseholdRequestPreviewDTO>;
          
          this.page = this.helperPendingRequests.number
          this.totalPages = this.helperPendingRequests.totalPages
          
          this.isLoadingPending = false;


          this.isLoadingDecision = false;
          
          console.log("nije null pending request details")
        }
      }
  }

  navigate(){

    
    const role = this.jwtService.getRole()?.toLowerCase()
    
    if(this.requestDetails && !this.requestDetails.isError){ this.router.navigate(['/home', role, 'realestate', this.requestDetails.data.realestateId, 'household', this.requestDetails.data.householdId]); }
  }


  navigateDetailsRequest(realestateId: number, householdId: number, requestId: number, modal: any){
    this.resetAndDismiss(modal)
    const role = this.jwtService.getRole()?.toLowerCase()
    this.router.navigate(['/home',role,'realestate', realestateId, 'household', householdId, 'household-request', requestId])
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
     
      this.requestDecision.emit({isAccepted: false, reason: reasonTemp.value})
      this.resetAndDismiss(modal)
      this.form.get('reason')?.reset('');
    }
   
  }


  openDenyDialog(){
    if (this.denyDialog) {
      this.isLoadingDecision = true;
      this.modalService.open(this.denyDialog, {
        centered: true,
        scrollable: true,
        backdrop: 'static',
      });
    }
  }

  openAcceptDialog(){
    if(this.helperPendingRequests){
      console.log("usli smo ovde helper ")

      this.isLoadingDecision = true;
      if(this.helperPendingRequests.content.length === 0){
        this.requestDecision.emit({isAccepted: true, reason: null})
      }else{
        if (this.acceptDialog) {
          this.modalService.open(this.acceptDialog, {
            centered: true,
            scrollable: true,
            backdrop: 'static',
          });
        }
      }
      
    }

    
  }


  acceptRequest(modal: any){
    this.requestDecision.emit({isAccepted: true, reason: null})
    this.resetAndDismiss(modal)
  }




  loadRequests(): void {
      this.isLoadingPending = true
      this.helperPendingRequests = null;
  
  
      this.pagingDetailsOutput.emit({page: this.page})
  
    }




  goToPage(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.page = page;
      this.loadRequests();
    }
  }
  
  goPrevious() {
    if (this.page > 0) {
      this.page--;
      this.loadRequests();
    }
  }

  goNext() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadRequests();
    }
  }
}
