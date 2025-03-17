import { AfterViewInit, Component, EventEmitter, Input, Output, ViewChild, NgZone, OnChanges, SimpleChanges } from '@angular/core';
import { debounceTime, distinctUntilChanged, switchMap, tap } from 'rxjs/operators';
import { FormControl } from '@angular/forms';
import { Subject } from 'rxjs';
import { ClientService } from '../../data-access/client.service';
import { CityDoc, MunicipalityDoc, RegionDoc, RealestateDoc } from '../../data-access/model/client-model';
import { FilteredSuggestion } from '../../feature/vacant-households/vacant-households.component';
import { LocationDTO } from '../../../../shared/model';
import { GoogleMap,  } from '@angular/google-maps';

@Component({
  selector: 'app-vacant-households-form',
  standalone: false,
  templateUrl: './vacant-households-form.component.html',
  styleUrl: './vacant-households-form.component.css'
})
export class VacantHouseholdsFormComponent implements AfterViewInit, OnChanges{

  @Input() filteredSuggestionsInput: FilteredSuggestion[] = []
  @Output() searchQueryE = new EventEmitter<string>();

  @Input() realestatesInput: RealestateDoc[] = [];
  @Output() searchAggregateE = new EventEmitter<{topLeft: LocationDTO, bottomRight: LocationDTO, zoomLevel: number, filterType?: string, filterDocId?: string}>();

  @ViewChild('googleMap') googleMap!: GoogleMap;
  //search
  selectedSuggestion: FilteredSuggestion | null = null;
  isSelectedSuggestion = false;
  showDropdown = false;
  searchControl = new FormControl('');
  //mapa

  private map!: google.maps.Map;
  private mapMoveSubject = new Subject<void>();
  center: google.maps.LatLngLiteral = { lat: 44.215341185649585, lng: 20.83940393242209 };
  mapOptions: google.maps.MapOptions = {
    zoom: 7,
    center: this.center,
    streetViewControl: false, 
    mapTypeControl: false,
    fullscreenControl: false,
    clickableIcons: false,
    cameraControl: false,
    mapId:"5e0fff8eb964f0f7"
  };
  realestates: {doc: RealestateDoc, marker: google.maps.marker.AdvancedMarkerElement}[] = [];

  highlightedRealestate: {doc: RealestateDoc, marker: google.maps.marker.AdvancedMarkerElement} | null = null;
  selectedRealestateMarker: {doc: RealestateDoc, marker: google.maps.marker.AdvancedMarkerElement} | null = null;
  popupPosition = { x: 0, y: 0 };


  protected isLoading = true;

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
      if(changes['realestatesInput']){
        
        this.realestates.forEach(realestate => {
          if (realestate.marker) {
            realestate.marker.map = null;  
          }
        });
        
        this.realestates = [];

        this.realestatesInput.forEach(doc => {
          let [lat, lon] = doc.location.split(",");



          //ovo sranje bi trebalo da radi medjutim iz nekog razloga ne prima eksterni smrdljivi css 
          const priceTag = document.createElement('div');
          
          priceTag.className = 'price-tag';
          console.log(priceTag)
          
          const shadowRoot = priceTag.attachShadow({mode: 'open'})
          
          //PA ONDA MORA NA OVAJ GLUPAV RETARDIRANI NACIN MRZIM CSS I JAVASCRIPT
         
          shadowRoot.innerHTML = `
          <style>
            .price-tag {
              background-color: #d11608;
              width: 10px;
              height: 10px;
              border-radius: 50%;
              border: solid 2.5px white
            }

            .price-tag:hover{
              background-color: green;
            }
          </style>
          <div class="price-tag"></div>`
          
          const marker = new google.maps.marker.AdvancedMarkerElement({
            map: this.map,
            position: { lat: parseFloat(lat), lng: parseFloat(lon) },
            content: priceTag,
          });

          marker.addListener('click', (event: google.maps.MapMouseEvent) => {

            if (!event.latLng) return;
            console.log("kurackernin")
            console.log(doc)
  
            const projection = (event as any).domEvent;
            console.log(projection)
  
            this.popupPosition = {
              x: projection.clientX,
              y: projection.clientY
            };
  
            console.log(this.popupPosition)

            if(this.selectedRealestateMarker) this.paintMarker(this.selectedRealestateMarker.marker, ' #d11608')
            this.paintMarker(marker, 'green')
            
            

            this.selectedRealestateMarker = {doc, marker};
            
          });

          this.realestates.push({doc, marker})
        });

        this.isLoading = false;
      }
  }

  paintMarker(marker: google.maps.marker.AdvancedMarkerElement, color: string){
    const markerElement = marker.content as HTMLElement;
    const markerShadow = markerElement.shadowRoot; 

    if (markerShadow) {
      const styleTag = markerShadow.querySelector("style");
      if (styleTag) {
        styleTag.innerHTML = `
          .price-tag {
            background-color:${color};
            width: 10px;
            height: 10px;
            border-radius: 50%;
            border: solid 2.5px white
          }`;
      }
    } else {
      console.error("ShadowRoot not found!");
    }
  }
  
  ngAfterViewInit() {

    if (this.googleMap && this.googleMap.googleMap) {
      this.map = this.googleMap.googleMap;
     
      this.map.addListener('zoom_changed', () => this.onMapMove());
      this.map.addListener('bounds_changed', () => this.onMapMove());
      this.map.addListener('click', () => {
       
        this.ngZone.run(() => {
          if(this.selectedRealestateMarker) this.paintMarker(this.selectedRealestateMarker?.marker, ' #d11608')
          this.selectedRealestateMarker = null; 
        });

      });
  
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
      if(this.selectedRealestateMarker) this.paintMarker(this.selectedRealestateMarker?.marker, ' #d11608')
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
      this.isLoading = true;
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


  highlightRealestate(highlight: boolean, realestate: {doc: RealestateDoc, marker: google.maps.marker.AdvancedMarkerElement}){
    if(highlight){
      this.paintMarker(realestate.marker, 'green')
      this.highlightedRealestate = realestate;
    } 
    else {
      this.paintMarker(realestate.marker, ' #d11608')
      this.highlightedRealestate = null;
    }

  }


}