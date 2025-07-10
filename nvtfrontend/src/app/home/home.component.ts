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
export class HomeComponent implements OnInit{
  
  center: google.maps.LatLngLiteral = { lat: 23.0225, lng: 72.5714}
  protected navbarExpanded = false;
  protected partialUserData: PartialUserData;
  protected imgUri: string = "";
  data: any[] | null = null;
  loginResponse: ResponseData;

  protected navbarVertical: boolean = false;
  resizeListener: (() => void) | undefined;


  
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
  
     
      this.getDataAndRedirect();
  }

  ngOnDestroy(): void {
    if (this.resizeListener) {
      window.removeEventListener('resize', this.resizeListener);
    }
  }

  ngOnInit(): void {
    this.checkScreenSize();
    this.resizeListener = () => this.checkScreenSize();
    window.addEventListener('resize', this.resizeListener);
  
    this.getDataAndRedirect();
  }

  checkScreenSize(): void {
    this.navbarVertical = window.innerWidth > 768;
  }


  signOut(): void{
 
    // this.jwtService.logout();
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

  //IZ NEKOG RAZLOGA OVO JE DA SE PODACI U HOME SCREENU CUVAJU
  getDataAndRedirect(){
 
    if(!this.jwtService.isLoggedIn()) return;
    
    
    this.jwtService.user$.subscribe(userData => {
      
      console.log("pokrecemo getData")
      if (userData) {
        
        console.log("vec ima podataka u home component get data")
        this.imgUri = userData.profileImage;
        this.partialUserData = userData.data;

        if (this.router.url === '/home' || this.router.url === '/home/') {
          this.redirectBasedOnRole(userData.data.role);
        }
      } 
      else {
        console.log("nema podataka u get data home component skidamo nove")
        
          
        forkJoin({
          profileImg: this.fileService.getProfileImage(),
          partialUserData: this.userService.getPartialUserData()
        }).subscribe({
          next: ({profileImg, partialUserData}) => {
            
          
            let imgUri: string = URL.createObjectURL(profileImg);
            this.partialUserData = partialUserData;
            this.imgUri = imgUri;
    
            this.jwtService.setUser({data: partialUserData, profileImage: imgUri})
            

            if (this.router.url === '/home' || this.router.url === '/home/') {
              this.redirectBasedOnRole(partialUserData.role);
            }
            console.log("HOMECOMPONENT")
            
          },
          error: (error) => {
            console.log(error)
            if([400, 401, 403].includes(error.status)) this.loginResponse = {isError: true, error: error.error as ResponseMessage}
            console.log("HOMECOMPONENT GRESKA")
          }
        })
      }
    })

  }


  private redirectBasedOnRole(role: string): void {
    switch (role) {
      case 'SUPERADMIN':
        this.router.navigate(['home/superadmin']);
        break;
      case 'CLIENT':
        this.router.navigate(['home/client']);
        break;
      default:
        this.router.navigate(['auth/login']);
        break;
    }
  }
  
  
}
