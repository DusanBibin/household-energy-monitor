import { Component, Query } from '@angular/core';
import { CityDoc, MunicipalityDoc, RegionDoc, RealestateDoc, RealestateImagePathsDTO } from '../../data-access/model/client-model';
import { ClientService } from '../../data-access/client.service';
import { filter } from 'rxjs';
import { LocationDTO } from '../../../../shared/model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-vacant-households',
  standalone: false,
  templateUrl: './vacant-households.component.html',
  styleUrl: './vacant-households.component.css'
})
export class VacantHouseholdsComponent {
  
  protected filteredSuggestions: FilteredSuggestion[] = [];
  protected realestatePins: RealestateDoc[] = [];
  protected realestatesImagePaths: RealestateImagePathsDTO[] = [];
  protected realestateImagesMap: Map<number, string[]> = new Map<number, string[]>();

  constructor(private clientService: ClientService, private router: Router){
  }


  handleRealestateNavigationDetails(realestateId: number){
    this.clientService.getVacantRealestateHouseHolds(realestateId).subscribe({
      next: ids => {
        console.log(ids)
      },
      error: err => {
        console.log(err)
      }
    })
    // this.router.navigate(['/home/client/realestate', realestateId]);
  }

  handleSearchAggregate(event: {topLeft: LocationDTO, bottomRight: LocationDTO, zoomLevel: number, filterType?: string, filterDocId?: string}){

    this.clientService.aggregate(event.topLeft, event.bottomRight, event.zoomLevel, event.filterType, event.filterDocId).subscribe({
      next: value => {

          this.realestatePins = value;
      },
      error: err => {
        console.log(err)
      }
    })
  }

  handleSearchQuery(query: string){
    this.clientService.searchVacantHousehold(query).subscribe({
      next: results => {
        this.filterSuggestions(results, query);
      },
      error: err => {console.log(err)}
    })
  }


  handleRealestateImagePaths(realestateIds: number[]){
    this.clientService.getRealestateImagePaths(realestateIds).subscribe({
      next: results => {
       

        let m = new Map<number, string[]>();

        for(const i of results){
          m.set(i.id, i.paths);
        }

        this.realestateImagesMap = m;
        
      },
      error: err => {console.log(err)}
    })
  }


  filterSuggestions(results: (CityDoc | MunicipalityDoc | RegionDoc | RealestateDoc)[], value: string) {
    if (!value) {
      this.filteredSuggestions = [];
      return;
    }

    const lowercaseValue = value.toLowerCase();
    this.filteredSuggestions = results

      .map((item: CityDoc | MunicipalityDoc | RegionDoc | RealestateDoc): FilteredSuggestion => {
        if ((item as CityDoc).city) {
          
          return {
            id: `${(item as CityDoc).id}`,
            dbId: (item as CityDoc).dbId,
            original: `${(item as CityDoc).city}`,
            highlighted: this.highlightMatch(`${(item as CityDoc).city}`, lowercaseValue),
            type: 'CITY',
          };
        } else if ((item as MunicipalityDoc).municipality) {
          
          return {

            id: `${(item as MunicipalityDoc).id}`,
            dbId: (item as CityDoc).dbId,
            original: `${(item as MunicipalityDoc).municipality}`,
            highlighted: this.highlightMatch(`${(item as MunicipalityDoc).municipality}`, lowercaseValue),
            type: 'MUNICIPALITY',
          };
        } else if ((item as RegionDoc).region) {
          
          return {
            id: `${(item as RegionDoc).id}`,
            dbId: (item as CityDoc).dbId,
            original: `${(item as RegionDoc).region}`,
            highlighted: this.highlightMatch(`${(item as RegionDoc).region}`, lowercaseValue),
            type: 'REGION',
          };
        } else {
          
          return {
            id: `${(item as RealestateDoc).id}`,
            dbId: (item as CityDoc).dbId,
            original: `${(item as RealestateDoc).address}`,
            highlighted: this.highlightMatch(`${(item as RealestateDoc).address}`, lowercaseValue),
            type: `${(item as RealestateDoc).type}`,
          };
        }
      });
  }


  highlightMatch(text: string, search: string): string {
    
    const words = search.trim().split(/\s+/).filter(word => word.length > 0);

    if (words.length === 0) return text; 

    const regex = new RegExp(`(${words.join("|")})`, "gi");

    return text.replace(regex, `<strong>$1</strong>`);
    
  }

}

export interface FilteredSuggestion{
  id:string,
  dbId:number,
  original:string,
  highlighted: string,
  type:string
}
