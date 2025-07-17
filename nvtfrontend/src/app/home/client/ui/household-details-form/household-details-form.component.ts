import { AfterViewInit, Component, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { Subject } from 'rxjs';
import { GoogleMap } from '@angular/google-maps';
import { HouseholdDetailsDTO } from '../../data-access/model/client-model';
import { environment } from '../../../../../environments/environment.development';

@Component({
  selector: 'app-household-details-form',
  standalone: false,
  templateUrl: './household-details-form.component.html',
  styleUrl: './household-details-form.component.css'
})
export class HouseholdDetailsFormComponent implements OnChanges{
  protected ENVIRONMENT_URL = environment.url

  @Input() householdDetails: HouseholdDetailsDTO | null = null;


  @ViewChild('googleMap') googleMap!: GoogleMap;
    private map!: google.maps.Map;
    private mapMoveSubject = new Subject<void>();
    center: google.maps.LatLngLiteral = { lat: 44.215341185649585, lng: 20.83940393242209 };
    mapOptions: google.maps.MapOptions = {
      zoom: 13,
      center: this.center,
      streetViewControl: false, 
      mapTypeControl: false,
      fullscreenControl: false,
      clickableIcons: false,
      cameraControl: false,
      mapId:"5e0fff8eb964f0f7"
    };
  
  markerPosition: google.maps.LatLngLiteral | null = null;

  constructor(){

  }

  ngOnChanges(changes: SimpleChanges){
    if(changes['householdDetails']){
      console.log("change se dogodio")
      console.log(this.householdDetails)
      
      if(this.householdDetails){
        let latitude = this.householdDetails.lat
        let longitude = this.householdDetails.lon


        this.markerPosition = {lat: latitude, lng: longitude}

        this.mapOptions = {
          ...this.mapOptions,
          zoom: 15, // 👈 or any zoom level you prefer
          center: this.markerPosition,
        };
      }
    }
  }
}
