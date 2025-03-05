import { Component, Query } from '@angular/core';
import { CityDoc, MunicipalityDoc, RegionDoc, RealestateDoc } from '../../data-access/model/client-model';
import { ClientService } from '../../data-access/client.service';
import { filter } from 'rxjs';
import { LocationDTO } from '../../../../shared/model';

@Component({
  selector: 'app-vacant-households',
  standalone: false,
  templateUrl: './vacant-households.component.html',
  styleUrl: './vacant-households.component.css'
})
export class VacantHouseholdsComponent {
  
  protected filteredSuggestions: FilteredSuggestion[] = [];
  protected realestatePins: RealestateDoc[] = [];

  constructor(private clientService: ClientService){

  }


  handleSearchAggregate(event: {topLeft: LocationDTO, bottomRight: LocationDTO, zoomLevel: number, filterType?: string, filterDocId?: string}){
    console.log("ides u kurac")
    this.clientService.aggregate(event.topLeft, event.bottomRight, event.zoomLevel, event.filterType, event.filterDocId).subscribe({
      next: value => {
          console.log(value)

          this.realestatePins = value;
          console.log(this.realestatePins);
      },
      error: err => {

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


  filterSuggestions(results: (CityDoc | MunicipalityDoc | RegionDoc | RealestateDoc)[], value: string) {
    console.log(results)
    if (!value) {
      this.filteredSuggestions = [];
      return;
    }

    const lowercaseValue = value.toLowerCase();
    this.filteredSuggestions = results

      .map((item: CityDoc | MunicipalityDoc | RegionDoc | RealestateDoc): FilteredSuggestion => {
        if ((item as CityDoc).city) {
          // item is of type CityDoc
          return {
            id: `${(item as CityDoc).id}`,
            original: `${(item as CityDoc).city}`,
            highlighted: this.highlightMatch(`${(item as CityDoc).city}`, lowercaseValue),
            type: 'CITY',
          };
        } else if ((item as MunicipalityDoc).municipality) {
          // item is of type MunicipalityDoc
          return {

            id: `${(item as MunicipalityDoc).id}`,
            original: `${(item as MunicipalityDoc).municipality}`,
            highlighted: this.highlightMatch(`${(item as MunicipalityDoc).municipality}`, lowercaseValue),
            type: 'MUNICIPALITY',
          };
        } else if ((item as RegionDoc).region) {
          // item is of type RegionDoc
          return {
            id: `${(item as RegionDoc).id}`,
            original: `${(item as RegionDoc).region}`,
            highlighted: this.highlightMatch(`${(item as RegionDoc).region}`, lowercaseValue),
            type: 'REGION',
          };
        } else {
          // item is of type RealestateDoc
          return {
            id: `${(item as RealestateDoc).id}`,
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
  id:string
  original:string,
  highlighted: string,
  type:string
}
