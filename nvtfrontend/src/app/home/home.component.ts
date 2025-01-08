import { Component, OnDestroy, OnInit } from '@angular/core';
import { JwtService } from '../shared/services/jwt-service/jwt.service';
import { Router } from '@angular/router';
import { CacheService } from '../shared/services/cache-service/cache.service';
import { Subscription, forkJoin } from 'rxjs';
import { FileService } from '../shared/services/file-service/file.service';
import { UserService } from '../shared/services/user-service/user.service';
import { PartialUserData, ResponseMessage } from '../shared/model';
import { ResponseData } from '../shared/model';

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrl: './home.component.css',
    standalone: false
})
export class HomeComponent implements OnInit, OnDestroy{
  
  center: google.maps.LatLngLiteral = { lat: 23.0225, lng: 72.5714}
 
  protected partialUserData: PartialUserData;
  protected imgUri: string = "";
  data: any[] | null = null;
  private cacheSubscription: Subscription;
  loginResponse: ResponseData;



  
  constructor(protected jwtService: JwtService, private router: Router, private cacheService: CacheService,
    private fileService: FileService, private userService: UserService){


      this.partialUserData = {
        email: "",
        name: "",
        lastname: ""
      }


      this.loginResponse = {
        isError: false,
        data: null
      }
  
      this.cacheSubscription = this.cacheService.cache$.subscribe(data => {
        this.data = data;
        this.imgUri= this.data?.[0];
        this.partialUserData = this.data?.[1] as PartialUserData;
      })

  }

  ngOnInit(): void {
    

    this.getData('userData');
  }


  signOut(): void{
    this.jwtService.logout();
    this.router.navigate(['../auth/login'])
  }

  ngOnDestroy(): void {
      this.cacheSubscription.unsubscribe();
      this.cacheService.clear('userData');
  }


  getData(key: string){
    console.log("da li smo usli ovdee")
    const cachedData = this.cacheService.get(key);
    console.log(cachedData)
    if(!cachedData){

      console.log("NEMA KESHA ULAZIMO DA UZMEMO OVO")
      forkJoin({
        profileImg: this.fileService.getProfileImage(),
        partialUserData: this.userService.getPartialUserData()
      }).subscribe({
        next: ({profileImg, partialUserData}) => {
      
          let imgUri: string = URL.createObjectURL(profileImg);
          this.cacheService.set('userData', [imgUri, partialUserData]);
        },
        error: (error) => {
          console.log("e ovde bas nismo")
          if(error.status == 400) this.loginResponse = {isError: true, error: error.error as ResponseMessage}
          console.log(this.loginResponse)
        }
      })
    }else{
      console.log("IMA KESHA ULAZIMO DA UZMEMO KESH");
      // const [imgUri, partialUserData] = cachedData;
    }


  }

  
  
}
