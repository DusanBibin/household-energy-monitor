import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ClientService } from '../../data-access/client.service';
import { error } from 'console';
import { HouseholdRequestPreviewDTO } from '../../data-access/model/client-model';

@Component({
  selector: 'app-household-requests-list',
  standalone: false,
  templateUrl: './household-requests-list.component.html',
  styleUrl: './household-requests-list.component.css'
})
export class HouseholdRequestsListComponent {

  
  status: string | null = null;
  sortField = 'requestSubmitted';
  sortDir = 'desc';

  requests: HouseholdRequestPreviewDTO[] = [];
  page = 0; 
  size = 10;
  totalPages = 0;
 
  kurac: number[] = Array(20).fill(0);
  isLoading = true;

  constructor(private route: ActivatedRoute, private clientService: ClientService){
    
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

  loadRequests(): void {
    this.isLoading = true;
    this.requests = [];

    this.clientService.getClientRequests(this.status, this.page, 10, this.sortField, this.sortDir).subscribe({
      next: (response: PagedResponse<HouseholdRequestPreviewDTO>) => {
        this.requests = response.content;
      
        this.page = response.number;
        this.totalPages = response.totalPages;
        this.isLoading = false;
        console.log(response)

      }, error: error => {
        console.log("neka greskaaa")
        console.log(error)
      }
    })
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






export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
