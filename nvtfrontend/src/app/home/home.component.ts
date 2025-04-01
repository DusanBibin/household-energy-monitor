import { Component, OnDestroy, OnInit } from '@angular/core';
import { JwtService } from '../shared/services/jwt-service/jwt.service';
import { Router } from '@angular/router';
import { Subscription, forkJoin } from 'rxjs';
import { FileService } from '../shared/services/file-service/file.service';
import { UserService } from '../shared/services/user-service/user.service';
import { PartialUserData, ResponseMessage } from '../shared/model';
import { ResponseData } from '../shared/model';
import { AuthService } from '../auth/data-access/auth.service';

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrl: './home.component.css',
    standalone: false
})
export class HomeComponent{
  
  center: google.maps.LatLngLiteral = { lat: 23.0225, lng: 72.5714}
 
  protected partialUserData: PartialUserData;
  protected imgUri: string = "";
  data: any[] | null = null;
  loginResponse: ResponseData;



  
  constructor(protected jwtService: JwtService, private router: Router,private fileService: FileService, private userService: UserService, private authService: AuthService){


      this.partialUserData = {
        email: "",
        name: "",
        lastname: "",
        role: ""
      }


      this.loginResponse = {
        isError: false,
        data: null
      }
  
     
      this.getData();
  }

  ngOnInit(): void {
  }


  signOut(): void{
    console.log("kurac1")
    // this.jwtService.logout();
    console.log("kurac2")
    
    
    console.log("kurac3")


    this.authService.logout().subscribe({
      next: value => {
        this.jwtService.setUser(null)
        this.router.navigate(['../auth/login'])
      },
      error: err => {
        console.log(err)
      }
    })
  }


  getData(){
 
    
    this.jwtService.user$.subscribe(userData => {
      if (userData) {
        console.log("Cached user data:", userData);
        console.log("usli smo u prvi deo ifa")


        this.imgUri = userData.profileImage;
        this.partialUserData = userData.data;
      } else {
        console.log("usli smo u drugi deo ifa")

        forkJoin({
          profileImg: this.fileService.getProfileImage(),
          partialUserData: this.userService.getPartialUserData()
        }).subscribe({
          next: ({profileImg, partialUserData}) => {
            
            console.log(partialUserData)
            let imgUri: string = URL.createObjectURL(profileImg);
            this.partialUserData = partialUserData;
            this.imgUri = imgUri;
    
            this.jwtService.setUser({data: partialUserData, profileImage: imgUri})
    
            console.log(imgUri)
            
          },
          error: (error) => {
            console.log("e ovde bas nismo")
            if(error.status == 400) this.loginResponse = {isError: true, error: error.error as ResponseMessage}
            console.log(this.loginResponse)
          }
        })
      }
    })
  
  
  
  
  }
  
  
}
