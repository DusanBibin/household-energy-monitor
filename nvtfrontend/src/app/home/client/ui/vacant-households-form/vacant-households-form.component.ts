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

  selectedSuggestion: FilteredSuggestion | null = null;
  isSelectedSuggestion = false;
  
  center: google.maps.LatLngLiteral = { lat: 44.215341185649585, lng: 20.83940393242209 };
  mapOptions: google.maps.MapOptions = {
    streetViewControl: false, 
    mapTypeControl: false,
    fullscreenControl: false,
    clickableIcons: false,
    cameraControl: false,
  };

 

  realestates: {doc: RealestateDoc, marker: google.maps.LatLngLiteral}[] = [];

  private map!: google.maps.Map;
  private mapMoveSubject = new Subject<void>(); 
  
  searchControl = new FormControl('');

  
  showDropdown = false;
  selectedRealestateMarker: {doc: RealestateDoc, marker: google.maps.LatLngLiteral} | null = null;

  popupPosition = { x: 0, y: 0 };

  constructor(private ngZone: NgZone) {
    this.searchControl.valueChanges
      .pipe(
        debounceTime(100), 
        distinctUntilChanged(),
        tap(value => {
          this.selectedRealestateMarker = null;
          if(this.isSelectedSuggestion) this.isSelectedSuggestion = false;
          else{
            this.selectedSuggestion = null;
            this.logMapDetails();
          } 
          
          if(value) value = encodeURIComponent(value.trim());
          this.searchQueryE.emit(value || '');
        })
      ).subscribe()
  }


  ngOnChanges(changes: SimpleChanges): void {
      if(changes['realestatePins']){
    
  
        
        this.realestates = [];

        this.realestatePins.forEach(doc => {
          let [lat, lon] = doc.location.split(",");
          let marker: google.maps.LatLngLiteral = {lat: parseFloat(lat), lng: parseFloat(lon)}
          this.realestates.push({doc, marker})
        });
      }
  }

  
  onMarkerClick(event: google.maps.MapMouseEvent, realestate: {doc: RealestateDoc, marker: google.maps.LatLngLiteral}){
    if (!event.latLng) return;

    console.log(realestate.doc.address)

    const projection = (event as any).domEvent;
    console.log(projection)
    // const position = projection.fromLatLngToDivPixel(event.latLng);

    this.popupPosition = {
      x: projection.clientX,
      y: projection.clientY
    };

    console.log(this.popupPosition)
    this.selectedRealestateMarker = realestate;
  }

  ngAfterViewInit() {

    if (this.googleMap && this.googleMap.googleMap) {
      this.map = this.googleMap.googleMap;
     
      this.map.addListener('zoom_changed', () => this.onMapMove());
      this.map.addListener('bounds_changed', () => this.onMapMove());
      this.map.addListener('click', () => this.onMapMove());
  
      this.mapMoveSubject.pipe(
        debounceTime(1000), 
        tap(() => {
          this.ngZone.run(() => {
            this.logMapDetails();
          });
        })
      ).subscribe();
    }
  }


  onMapMove() {
    this.ngZone.run(() => {
      this.selectedRealestateMarker = null
    });
    this.mapMoveSubject.next();
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
   
      if(this.selectedSuggestion != null){  
    
        filterType = this.selectedSuggestion.type;
        filterDocId = this.selectedSuggestion.id;
      }
      
      let zoomLevel: number = zoom!;
  
      this.searchAggregateE.emit({topLeft, bottomRight, zoomLevel, filterType, filterDocId});
    }
  }

  
  selectSuggestion(suggestion: FilteredSuggestion) {
    this.searchControl.setValue(suggestion.original);
    this.showDropdown = false;
    if(['CITY', 'MUNICIPALITY', 'REGION'].includes(suggestion.type)){
      this.isSelectedSuggestion = true;
      this.selectedSuggestion = suggestion;
      this.logMapDetails();
    } 

  }

  hideDropdown() {
    setTimeout(() => {
      this.showDropdown = false;
    }, 100);
  }
}


