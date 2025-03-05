import { AfterViewInit, Component, EventEmitter, Input, Output, ViewChild, NgZone, OnChanges, SimpleChanges } from '@angular/core';
import { debounceTime, distinctUntilChanged, switchMap, tap } from 'rxjs/operators';
import { FormControl } from '@angular/forms';
import { Subject } from 'rxjs';
import { ClientService } from '../../data-access/client.service';
import { CityDoc, MunicipalityDoc, RegionDoc, RealestateDoc } from '../../data-access/model/client-model';
import { FilteredSuggestion } from '../../feature/vacant-households/vacant-households.component';
import { LocationDTO } from '../../../../shared/model';
@Component({
  selector: 'app-vacant-households-form',
  standalone: false,
  templateUrl: './vacant-households-form.component.html',
  styleUrl: './vacant-households-form.component.css'
})
export class VacantHouseholdsFormComponent implements AfterViewInit, OnChanges{

  @Input() filteredSuggestions: FilteredSuggestion[] = []
  @Output() searchQueryE = new EventEmitter<string>();

  @Input() realestatePins: RealestateDoc[] = [];
  @Output() searchAggregateE = new EventEmitter<{topLeft: LocationDTO, bottomRight: LocationDTO, zoomLevel: number, filterType?: string, filterDocId?: string}>();

  @ViewChild('googleMap') googleMap!: any;

  selectedSuggestion: FilteredSuggestion | null= null;
  
  center: google.maps.LatLngLiteral = { lat: 44.215341185649585, lng: 20.83940393242209 };
  mapOptions: google.maps.MapOptions = {
    streetViewControl: false, 
    mapTypeControl: false,
    fullscreenControl: false,
    clickableIcons: false,
    cameraControl: false
  };

  markers: google.maps.LatLngLiteral[] = [];

  private map!: google.maps.Map;
  private mapMoveSubject = new Subject<void>(); 
  
  searchControl = new FormControl('');

  
  showDropdown = false;

  constructor(private ngZone: NgZone) {
    this.searchControl.valueChanges
      .pipe(
        debounceTime(100), 
        distinctUntilChanged(),
        tap(value => {
          console.log(value)
          if(value) value = encodeURIComponent(value.trim());
          console.log(value)
          this.searchQueryE.emit(value || '');
        })
      ).subscribe()
  }


  ngOnChanges(changes: SimpleChanges): void {
      if(changes['realestatePins']){
        console.log("KURAC")
        console.log(this.realestatePins)
        
        this.markers = [];

        this.realestatePins.forEach(realestate => {
          let [lat, lon] = realestate.location.split(",");
          this.markers.push({lat: parseFloat(lat), lng: parseFloat(lon)})
        });
      }
  }

  ngAfterViewInit() {
    console.log("iksdebro1")
    console.log(this.googleMap)
    console.log(this.googleMap.googleMap)
    if (this.googleMap && this.googleMap.googleMap) {
      this.map = this.googleMap.googleMap;

      console.log("iksdebro2")
     
      this.map.addListener('zoom_changed', () => this.mapMoveSubject.next());
      this.map.addListener('bounds_changed', () => this.mapMoveSubject.next());

  
      this.mapMoveSubject.pipe(
        debounceTime(2000), 
        tap(() => {
          this.ngZone.run(() => {
            this.logMapDetails();
          });
        })
      ).subscribe();
    }
  }

  logMapDetails() {
    if (!this.map) return;

    const zoom = this.map.getZoom();
    const bounds = this.map.getBounds();

    if (bounds) {

      const northEast = bounds.getNorthEast(); 
      const southWest = bounds.getSouthWest(); 

      console.log('Zoom Level:', zoom);
      console.log('Bounding Box:');
      console.log('Top Left (NW):', { lat: northEast.lat(), lng: southWest.lng() });
      console.log('Bottom Right (SE):', { lat: southWest.lat(), lng: northEast.lng() });
      
      let topLeft: LocationDTO = {
        lon: southWest.lng(),
        lat: northEast.lat()
      }

      let bottomRight: LocationDTO = {
        lon: northEast.lng(),
        lat: southWest.lat()
      }

      let filterType: string = "";
      let filterDocId: string = "";
      let zoomLevel: number = zoom!;

      this.searchAggregateE.emit({topLeft, bottomRight, zoomLevel, filterType, filterDocId});
    }
  }

  
  selectSuggestion(suggestion: FilteredSuggestion) {
    this.searchControl.setValue(suggestion.original);
    this.showDropdown = false;

    if(['CITY', 'MUNICIPALITY', 'REGION'].includes(suggestion.type)) this.selectedSuggestion = suggestion;


  }

  hideDropdown() {
    setTimeout(() => {
      this.showDropdown = false;
    }, 100);
  }
}


