import { Component } from '@angular/core';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { FormControl } from '@angular/forms';
import { ClientService } from '../../data-access/client.service';
import { RealestateDoc } from '../../data-access/model/client-model';

@Component({
  selector: 'app-vacant-households-form',
  standalone: false,
  templateUrl: './vacant-households-form.component.html',
  styleUrl: './vacant-households-form.component.css'
})
export class VacantHouseholdsFormComponent {
  searchControl = new FormControl('');
  suggestions: string[] = [
    'New York', 'Los Angeles', 'San Francisco', 'Chicago', 'Houston',
    'Seattle', 'Miami', 'Boston', 'Austin', 'Denver'
  ];
  filteredSuggestions: { original: string, highlighted: string }[] = [];
  showDropdown = false;

  constructor(private clientService: ClientService) {
    this.searchControl.valueChanges
      .pipe(
        debounceTime(200), 
        distinctUntilChanged(),
        switchMap(value => this.clientService.searchVacantHousehold(value || ''))
      )
      .subscribe(results => this.filterSuggestions(results, this.searchControl.value || ''));
  }

  filterSuggestions(results: RealestateDoc[], value: string) {
    console.log(results)
    if (!value) {
      this.filteredSuggestions = [];
      return;
    }

    const lowercaseValue = value.toLowerCase();
    this.filteredSuggestions = results
      // .filter(item =>
      //   item.address.toLowerCase().includes(lowercaseValue) ||
      //   item.city.toLowerCase().includes(lowercaseValue) ||
      //   item.municipality.toLowerCase().includes(lowercaseValue) ||
      //   item.region.toLowerCase().includes(lowercaseValue)
      // )
      .map(item => ({
        original: `${item.address}, ${item.city}, ${item.municipality}, ${item.region}`,
        highlighted: this.highlightMatch(`${item.address}, ${item.city}, ${item.municipality}, ${item.region}`, lowercaseValue)
      }));
  }

  highlightMatch(text: string, search: string): string {
    const regex = new RegExp(`(${search})`, 'gi');
    return text.replace(regex, `<strong>$1</strong>`);
  }

  selectSuggestion(suggestion: { original: string, highlighted: string }) {
    this.searchControl.setValue(suggestion.original);
    this.showDropdown = false;
  }

  hideDropdown() {
    setTimeout(() => {
      this.showDropdown = false;
    }, 200);
  }
}
