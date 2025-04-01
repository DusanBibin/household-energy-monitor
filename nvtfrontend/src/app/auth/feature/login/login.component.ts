import { Component, ViewEncapsulation } from '@angular/core';
import { AuthRequestDTO, AuthResponseDTO } from '../../data-access/model/auth-model';
import { AuthService } from '../../data-access/auth.service';
import { ResponseMessage, ResponseData, PartialUserData } from '../../../shared/model';
import { JwtService } from '../../../shared/services/jwt-service/jwt.service';
import { Router } from '@angular/router';
import { SnackBarService } from '../../../shared/services/snackbar-service/snackbar.service';
import { FileService } from '../../../shared/services/file-service/file.service';
import { error } from 'console';
import { forkJoin } from 'rxjs';
import { UserService } from '../../../shared/services/user-service/user.service';

@Component({
    selector: 'app-auth',
    templateUrl: './login.component.html',
    styleUrl: './login.component.css',
    standalone: false
})
export class LoginComponent {

  loginResponse: ResponseData;
  constructor(private router: Router, private authService: AuthService, private jwtService: JwtService, 
    private snackBar: SnackBarService, private fileService: FileService, private userService: UserService){
    this.loginResponse = {
      isError: false,
      data: null
    }
  }

  handleLoginData(formData: AuthRequestDTO): void {
    console.log(formData)


    if(!(this.jwtService.isLoggedIn() && this.jwtService.hasRole(['SUPERADMIN']))){
      this.authService.logout().subscribe({
        next: value => {

          this.jwtService.setUser(null)


          this.authService.login(formData).subscribe(
            {
              next: (data) => {
                console.log("uspelo")
                let d: PartialUserData = data as PartialUserData;
      
                this.loginResponse = {isError: false, data: d}
      
                
                this.fileService.getProfileImage().subscribe({
                  next: profileImg => {
                    let imgUri: string = URL.createObjectURL(profileImg);
      
                    this.jwtService.setUser({data: d, profileImage: imgUri})
                    
      
                    this.snackBar.openSnackBar("Login successful")
                    if(this.jwtService.hasRole(["SUPERADMIN"])) { 
                      console.log("OVO JE SUPERADMIN")
          
                      if(!this.jwtService.isFirstSuperadminLogin()){
                        this.router.navigate(['/home'], {replaceUrl: true})
                        
                      }else{
                        this.router.navigate(['/home'], {replaceUrl: true})
                      }
                    }else{
                      this.router.navigate(['/home'], {replaceUrl: true})
                    } 
                        
                  },
                  error: err => {
                    console.log(err)
                  }
                })
      
      
              },
              error: (error) => {
              
                if(error.status == 400) this.loginResponse = {isError: true, error: error.error as ResponseMessage}
                else{
                  console.log(error)
                  this.loginResponse = {isError: true, error: {message: "Unknown error"}}
                }
                
                
              }
              
            }
          );



          
        },
        error: error => {
          console.log(error)
        }
      })
    }else {
      this.authService.login(formData).subscribe(
        {
          next: (data) => {
            console.log("uspelo")
            let d: PartialUserData = data as PartialUserData;
  
            this.loginResponse = {isError: false, data: d}
  
            
            this.fileService.getProfileImage().subscribe({
              next: profileImg => {
                let imgUri: string = URL.createObjectURL(profileImg);
  
                this.jwtService.setUser({data: d, profileImage: imgUri})
                
  
                this.snackBar.openSnackBar("Login successful")
                if(this.jwtService.hasRole(["SUPERADMIN"])) { 
                  console.log("OVO JE SUPERADMIN")
      
                  if(!this.jwtService.isFirstSuperadminLogin()){
                    this.router.navigate(['/home'], {replaceUrl: true})
                    
                  }else{
                    this.router.navigate(['/home'], {replaceUrl: true})
                  }
                }else{
                  this.router.navigate(['/home'], {replaceUrl: true})
                } 
                    
              },
              error: err => {
                console.log(err)
              }
            })
  
  
          },
          error: (error) => {
          
            if(error.status == 400) this.loginResponse = {isError: true, error: error.error as ResponseMessage}
            else{
              console.log(error)
              this.loginResponse = {isError: true, error: {message: "Unknown error"}}
            }
            
            
          }
          
        }
      );
    }

  }



  

}


