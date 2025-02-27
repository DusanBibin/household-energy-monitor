import { Component } from '@angular/core';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { FormControl } from '@angular/forms';
import { ClientService } from '../../data-access/client.service';
import { CityDoc, MunicipalityDoc, RegionDoc, RealestateDoc } from '../../data-access/model/client-model';

@Component({
  selector: 'app-vacant-households-form',
  standalone: false,
  templateUrl: './vacant-households-form.component.html',
  styleUrl: './vacant-households-form.component.css'
})
export class VacantHouseholdsFormComponent {
  center: google.maps.LatLngLiteral = { lat: 40.73061, lng: -73.935242 };
  mapOptions: google.maps.MapOptions = {
    streetViewControl: false, 
    mapTypeControl: false,
    fullscreenControl: false,
    clickableIcons: false
  };
  
  searchControl = new FormControl('');

  filteredSuggestions: { original: string, highlighted: string, type:string }[] = [];
  showDropdown = false;

  constructor(private clientService: ClientService) {
    this.searchControl.valueChanges
      .pipe(
        debounceTime(100), 
        distinctUntilChanged(),
        switchMap(value => this.clientService.searchVacantHousehold(value || ''))
      )
      .subscribe(results => this.filterSuggestions(results, this.searchControl.value || ''));
  }

  filterSuggestions(results: (CityDoc | MunicipalityDoc | RegionDoc | RealestateDoc)[], value: string) {
    console.log(results)
    if (!value) {
      this.filteredSuggestions = [];
      return;
    }

    const lowercaseValue = value.toLowerCase();
    this.filteredSuggestions = results

      .map((item: CityDoc | MunicipalityDoc | RegionDoc | RealestateDoc) => {
        if ((item as CityDoc).city) {
          // item is of type CityDoc
          return {
            original: `${(item as CityDoc).city}`,
            highlighted: this.highlightMatch(`${(item as CityDoc).city}`, lowercaseValue),
            type: 'CITY',
          };
        } else if ((item as MunicipalityDoc).municipality) {
          // item is of type MunicipalityDoc
          return {
            original: `${(item as MunicipalityDoc).municipality}`,
            highlighted: this.highlightMatch(`${(item as MunicipalityDoc).municipality}`, lowercaseValue),
            type: 'MUNICIPALITY',
          };
        } else if ((item as RegionDoc).region) {
          // item is of type RegionDoc
          return {
            original: `${(item as RegionDoc).region}`,
            highlighted: this.highlightMatch(`${(item as RegionDoc).region}`, lowercaseValue),
            type: 'REGION',
          };
        } else {
          // item is of type RealestateDoc
          return {
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

  selectSuggestion(suggestion: { original: string, highlighted: string }) {
    this.searchControl.setValue(suggestion.original);
    this.showDropdown = false;
  }

  hideDropdown() {
    setTimeout(() => {
      this.showDropdown = false;
    }, 100);
  }
}
