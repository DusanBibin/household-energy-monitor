import { Component, Input, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import { HouseholdSummaryDTO, RealestateSummaryDTO } from '../../data-access/model/client-model';
import { environment } from '../../../../../environments/environment.development';
import { PagedResponse } from '../household-requests-list/household-requests-list.component';
import { Router } from '@angular/router';
import { FormControl, Validators } from '@angular/forms';
import { TextUtilServiceService } from '../../../../shared/services/text-util-service/text-util.service';
import { JwtService } from '../../../../shared/services/jwt-service/jwt.service';
@Component({
  selector: 'app-realestate-list-dumb',
  standalone: false,
  
  templateUrl: './realestate-list-dumb.component.html',
  styleUrl: './realestate-list-dumb.component.css'
})
export class RealestateListDumbComponent implements OnChanges{
  isLoading = true;

  totalPages = 0;
  page = 1; 
  selectedId = 0;

  constructor(private router: Router, private textService: TextUtilServiceService, private jwtService: JwtService) {}


  realestates: RealestateSummaryDTO[] | null = null;
  @Input() realestatesPagedResponse: PagedResponse<RealestateSummaryDTO> | null = null;
  @Output() pageEmitter = new EventEmitter<number>();

  ENVIRONMENT_URL = environment.apiUrl + "/file/realestate"


  apartmentSearchControl = new FormControl('', [Validators.pattern(/^\d*$/)]);

  apartmentSelectionPosition = {location: {x:0, y:0}, buildingSelected: false, clicked: false};
  filteredVacantRealestateApartmentsIds: HouseholdSummaryDTO[] = [];
  vacantRealestateApartmentsIds: HouseholdSummaryDTO[] = [];
  apartmentSelectionPopupSelected = false;
  selectedRealestate: RealestateSummaryDTO | null = null;


  ngOnChanges(changes: SimpleChanges): void {
      if(changes['realestatesPagedResponse']){
        if(this.realestatesPagedResponse){
         
          this.page = this.realestatesPagedResponse.number;
          this.totalPages = this.realestatesPagedResponse.totalPages;
          this.realestates = this.realestatesPagedResponse.content;
          this.isLoading = false;
        }  
      }
  }



  loadRequests(): void {
      this.isLoading = true;
  
    
      // let data: PagingDetails = { status: this.status, page: this.page, sortField: this.sortField, sortDir: this.sortDir}
  
      this.pageEmitter.emit(this.page)
  
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


  


  registerClick(event: MouseEvent){
  
    // ovo uvek brise 
    




    this.apartmentSearchControl.setValue("")

    if(this.apartmentSelectionPopupSelected){
      this.apartmentSelectionPopupSelected = false;
    }else{
      
      if(this.apartmentSelectionPosition.clicked === true) this.apartmentSelectionPosition.clicked = false;
      else {
        this.vacantRealestateApartmentsIds = []
        this.selectedRealestate = null;
        this.apartmentSelectionPosition.buildingSelected = false
      }
    }

     
  }

  highlightApartmentNumber(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
    this.apartmentSearchControl.setValue(input.value, { emitEvent: false });

    
    this.filteredVacantRealestateApartmentsIds = [];


    let prefix = this.apartmentSearchControl.value;
    console.log(prefix)
    if(prefix){
      for(const apartment of this.vacantRealestateApartmentsIds){
        if(apartment.apartmentNumber.startsWith(prefix)){

          let apartmentNum = this.textService.highlightMatch(apartment.apartmentNumber, prefix);
         
          let apartmentDTO: HouseholdSummaryDTO = {
            id: apartment.id,
            apartmentNumber: apartmentNum
          }

          this.filteredVacantRealestateApartmentsIds.push(apartmentDTO);
        }
      }
    }else this.filteredVacantRealestateApartmentsIds = this.vacantRealestateApartmentsIds
    
  }

  clickPopup(event: MouseEvent){
   
    this.apartmentSelectionPopupSelected = true;
  }



  routeDetailsPage(realestate: RealestateSummaryDTO, event: MouseEvent){
      

      this.apartmentSelectionPosition.location.x = event.clientX;
      this.apartmentSelectionPosition.location.y = event.clientY;


      // if(event.clientX + 250 >= window.innerWidth){
      //   this.apartmentSelectionPosition.location.x = event.clientX - 250;
      // }

      // if(event.clientY + 250 >= window.innerHeight){
      //   this.apartmentSelectionPosition.location.y = event.clientY - 250;
      // }

      // if(event.clientX - 250 <= 0){
      //   this.apartmentSelectionPosition.location.x = event.clientX + 250;
      // }

      // if(event.clientY - 250 <= 0 ){
      //   this.apartmentSelectionPosition.location.y = event.clientY + 250;
      // }
      
      this.apartmentSelectionPosition.location.x = event.clientX;
      this.apartmentSelectionPosition.location.y = event.clientY;
        
      this.apartmentSelectionPosition.clicked = true;
        //250 px je zato sto je u vacant-households-form-component.html max-width i max-height 250px
      
      this.filteredVacantRealestateApartmentsIds = realestate.householdSummaries;
      this.vacantRealestateApartmentsIds = realestate.householdSummaries;
      console.log(this.vacantRealestateApartmentsIds)
      this.selectedRealestate = realestate;
    
        // console.log(realestateDoc)
      if(realestate){
        if(realestate.type === 'BUILDING' && realestate.householdSummaries.length > 1){
      
          this.apartmentSelectionPosition.buildingSelected = true;
        }else{
          this.apartmentSelectionPosition.buildingSelected = false;

          let household = realestate.householdSummaries[0];
          const role = this.jwtService.getRole()?.toLowerCase()
     
          this.router.navigate(['/home', role, 'realestate', realestate.realestateId, 'household', household.id]);
        }
      
      }else{
        // console.log("Nema realestatea")
      }

  }

  navigateHouseholdDetails(householdId: number){
    const role = this.jwtService.getRole()?.toLowerCase()
    console.log("jel udje ovde")
    this.router.navigate(['/home', role, 'realestate', this.selectedRealestate?.realestateId, 'household', householdId]);
  }
  
    
    

   
}
