import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClientService } from '../../data-access/client.service';
import { RealestateSummaryDTO } from '../../data-access/model/client-model';
import { PagedResponse } from '../../ui/household-requests-list/household-requests-list.component';
@Component({
  selector: 'app-realestate-list',
  standalone: false,
  templateUrl: './realestate-list.component.html',
  styleUrl: './realestate-list.component.css'
})
export class RealestateListComponent implements OnInit {

  page!: number;


  realestates: PagedResponse<RealestateSummaryDTO> | null = null;
  constructor(private route: ActivatedRoute, private clientService: ClientService, private router: Router) {}

  ngOnInit(): void {
    
    this.route.queryParamMap.subscribe(params => {
      const pageParam = params.get('page');
      this.page = pageParam ? Number(pageParam) : 1; 
      this.page = this.page - 1;


      console.log(this.page)
      this.fetchRealestates(this.page);

      
    });

  }


  handlePageChange(page: number){
    this.fetchRealestates(page)
  }


  private fetchRealestates(page: number): void {
    this.clientService.getRealestates(page, 10).subscribe({
      next: values => {
        console.log(values)
        
        
        if (this.page > values.totalPages) {
        
          this.router.navigate([], {
            relativeTo: this.route,
            queryParams: { page: values.totalPages },
            queryParamsHandling: 'merge',
            replaceUrl: true
          });
          return; 
        }

        values.content.forEach(realestate => {
          if (!realestate.images || realestate.images.length === 0) {
            realestate.images = [
              'realestate1.png',
              'realestate2.png',
              'realestate3.png',
              'realestate4.png'
            ];
          }
        });
        this.realestates = values;

        console.log(this.realestates)
      }, error: err => {
        console.log(err);
      }
    })
  }
}
