import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { Subject } from 'rxjs';
import { GoogleMap } from '@angular/google-maps';
@Component({
  selector: 'app-household-details-form',
  standalone: false,
  templateUrl: './household-details-form.component.html',
  styleUrl: './household-details-form.component.css'
})
export class HouseholdDetailsFormComponent implements AfterViewInit{
  
  @ViewChild('googleMap') googleMap!: GoogleMap;
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
  
  constructor(){

  }

  ngAfterViewInit(): void {
      
  }
}
