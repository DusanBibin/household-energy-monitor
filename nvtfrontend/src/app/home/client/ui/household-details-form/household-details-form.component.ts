import { AfterViewInit, Component, Input, OnChanges, SimpleChanges, ViewChild, TemplateRef, Output, EventEmitter } from '@angular/core';
import { Subject } from 'rxjs';
import { GoogleMap } from '@angular/google-maps';
import { HouseholdDetailsDTO } from '../../data-access/model/client-model';
import { environment } from '../../../../../environments/environment.development';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SnackBarService } from '../../../../shared/services/snackbar-service/snackbar.service';


@Component({
  selector: 'app-household-details-form',
  standalone: false,
  templateUrl: './household-details-form.component.html',
  styleUrl: './household-details-form.component.css'
})
export class HouseholdDetailsFormComponent implements OnChanges{
  protected ENVIRONMENT_URL = environment.url

  @Input() householdDetails: HouseholdDetailsDTO | null = null;
  @Output() requestClaimFiles = new EventEmitter<{ id: number, file: File | null }[]>();

  @ViewChild('fileDialog') fileDialog: TemplateRef<any> | null = null;


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


  protected files: { id: number, file: File | null }[] = [{ id: Date.now(), file: null }];



  constructor(private modalService: NgbModal, private snackService: SnackBarService){

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

  onFileSelected(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
  
      const allowedTypes = ['application/pdf', 'image/png', 'image/jpeg'];
  
      if (!allowedTypes.includes(file.type)) {
        this.snackService.openSnackBar("Only PDF, PNG, and JPG files are allowed.");
        input.value = ''; // Clear the invalid file
        return;
      }
  
      this.files[index].file = file;
  
      // Only add a new input if the last one has a file
      const isLast = index === this.files.length - 1;
      if (isLast) {
        this.files.push({ id: Date.now() + Math.random(), file: null });
      }
    }
  }
  

  openFileDialog(){
    if (this.fileDialog) {
      this.modalService.open(this.fileDialog, {
        centered: true,
        scrollable: true,
        backdrop: 'static',
      });
    }
  }

  resetAndDismiss(modal: any): void {
    modal.dismiss('Cancel click');
    this.files = [{ id: Date.now(), file: null }]
  }

  removeItem(index: number): void {
    this.files.splice(index, 1);
  
    // If all inputs are removed, make sure there's at least one empty
    if (this.files.length === 0) {
      this.files.push({ id: Date.now(), file: null });
    }
  }

  submitClaimRequest(modal: any){
    if(this.files[0].file === null) {
      this.snackService.openSnackBar("You need to upload at least one file")
      return;
    }
    this.requestClaimFiles.emit(this.files);
    this.resetAndDismiss(modal)
  }
  
  

 
}
