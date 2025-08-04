import { AfterViewInit, Component, Input, OnChanges, SimpleChanges, ViewChild, TemplateRef, Output, EventEmitter } from '@angular/core';
import { Subject } from 'rxjs';
import { GoogleMap } from '@angular/google-maps';
import { ConsumptionDTO, HouseholdDetailsDTO } from '../../data-access/model/client-model';
import { environment } from '../../../../../environments/environment.development';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SnackBarService } from '../../../../shared/services/snackbar-service/snackbar.service';
import { JwtService } from '../../../../shared/services/jwt-service/jwt.service';
import { ResponseData } from '../../../../shared/model';


@Component({
  selector: 'app-household-details-form',
  standalone: false,
  templateUrl: './household-details-form.component.html',
  styleUrl: './household-details-form.component.css'
})
export class HouseholdDetailsFormComponent implements OnChanges{
  protected ENVIRONMENT_URL = environment.url
  envProfileImg = environment.apiUrl + "/file/profile-img/"
  @Input() householdDetails: ResponseData | null = null;
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

  

  @Input() chartData: ConsumptionDTO[] | null = null;
  @Output() changeMonthEmitter =  new EventEmitter<{ isForward: boolean}>();


  constructor(private modalService: NgbModal, private snackService: SnackBarService, protected jwtService: JwtService){}
  
  chartOption: any;
  ngOnInit(): void {
    this.chartOption = {
      title: {
        text: 'Example Bar Chart'
      },
      tooltip: {},
      legend: {
        data: ['Sales']
      },
      xAxis: {
        data: ['Shirts', 'Cardigans', 'Chiffons', 'Pants', 'Heels', 'Socks']
      },
      yAxis: {},
      series: [{
        name: 'Sales',
        type: 'bar',
        data: [5, 20, 36, 10, 10, 20]
      }]
    };
  }

  // Makes chart responsive
  onChartInit(ec: any) {
    window.addEventListener('resize', () => {
      ec.resize();
    });
  }



  ngOnChanges(changes: SimpleChanges){
    if(changes['householdDetails']){
     
      console.log(this.householdDetails)
      
      if(this.householdDetails && !this.householdDetails.isError){
        let latitude = this.householdDetails.data.lat
        let longitude = this.householdDetails.data.lon


        this.markerPosition = {lat: latitude, lng: longitude}

        this.mapOptions = {
          ...this.mapOptions,
          zoom: 15, // 👈 or any zoom level you prefer
          center: this.markerPosition,
        };
      }
    }


    if(changes['chartData'] && this.chartData){
      if(this.chartData.length != 0){
        console.log(this.chartData)

        const months = this.chartData.map(d => d.datetime);
        const kwhValues = this.chartData.map(d => d.kwh);

        // update the chart option
        this.chartOption = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'  // shows a shadow over the bar hovered
            },
            formatter: (params: any) => {
              // params is an array because trigger:'axis' returns all series on that axis,
              // but here we only have 1 series, so params[0] is the one.
              const param = params[0];
              return `${param.name}<br/>${param.seriesName}: ${param.data}`;
            }
          },
          xAxis: {
            type: 'category',
            data: months
          },
          yAxis: {
            type: 'value'
          },
          series: [{
            name: 'kWh',
            type: 'bar',
            data: kwhValues
          }]
        };
      }
    }
  }


  changeMonth(isForward: boolean){
    this.changeMonthEmitter.emit({isForward});
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
