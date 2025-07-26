import { Component, Input, Output, EventEmitter, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClientService } from '../../data-access/client.service';
import { error } from 'console';
import { HouseholdRequestPreviewDTO } from '../../data-access/model/client-model';
import { request } from 'http';


@Component({
  selector: 'app-household-requests-list',
  standalone: false,
  templateUrl: './household-requests-list.component.html',
  styleUrl: './household-requests-list.component.css'
})
export class HouseholdRequestsListComponent {


  @Input() pagingDetailsInput: PagedResponse<HouseholdRequestPreviewDTO> | null = null;
  @Output() pagingDetailsOutput = new EventEmitter<PagingDetails>();
  
  status: string | null = null;
  sortField = 'requestSubmitted';
  sortDir = 'desc';

  requests: HouseholdRequestPreviewDTO[] = [];
  page = 0; 
  size = 10;
  totalPages = 0;
 
  isLoading = true;

  constructor(private route: ActivatedRoute, private clientService: ClientService, private router: Router){
    
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const statusParam = params.get('status');
      const sortFieldParam = params.get('sortField');
      const sortDirParam = params.get('sortDir');

      this.status = ['PENDING', 'ACCEPTED', 'REJECTED'].includes(statusParam ?? '') ? statusParam : null;
      
      const rawPageParam = +(params.get('page') || 1); 

  
      const safePageParam = isNaN(rawPageParam) || rawPageParam < 1 ? 1 : rawPageParam;

  
      this.page = safePageParam - 1;

      this.sortField = ['requestSubmitted', 'requestProcessed'].includes(sortFieldParam ?? '') ? sortFieldParam! : 'requestSubmitted';
      this.sortDir = (sortDirParam === 'asc' || sortDirParam === 'desc') ? sortDirParam : 'desc';

      this.loadRequests();
    });


    console.log(this.status)
    console.log(this.page)
    console.log(this.sortField)
    console.log(this.sortDir)
  }


  ngOnChanges(changes: SimpleChanges): void {


      if(changes['pagingDetailsInput']){
        console.log("change se dogodio")
        console.log(this.pagingDetailsInput)
        
        if(this.pagingDetailsInput){
          this.requests = this.pagingDetailsInput?.content
          this.page = this.pagingDetailsInput?.number
          this.totalPages = this.pagingDetailsInput.totalPages
          this.isLoading = false;
        } 

      }
  
    }



  loadRequests(): void {
    this.isLoading = true;
    this.requests = [];


    let data: PagingDetails = { status: this.status, page: this.page, sortField: this.sortField, sortDir: this.sortDir}

    this.pagingDetailsOutput.emit(data)

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

  navigateDetailsRequest(realestateId: number, householdId: number, requestId: number){
    this.router.navigate(['/home/client/realestate', realestateId, 'household', householdId, 'household-request', requestId])
  }

}


export interface PagingDetails {
  status: string | null,
  page: number,
  sortField: string,
  sortDir: string
}



export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
