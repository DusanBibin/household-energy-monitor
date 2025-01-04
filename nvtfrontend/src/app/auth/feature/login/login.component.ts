import { Component, ViewEncapsulation } from '@angular/core';
import { AuthRequestDTO, AuthResponseDTO } from '../../data-access/model/auth-model';
import { AuthService } from '../../data-access/auth.service';
import { ResponseMessage, ResponseData } from '../../../shared/model';
import { JwtService } from '../../../shared/services/jwt-service/jwt.service';
import { Router } from '@angular/router';
import { SnackBarService } from '../../../shared/services/snackbar-service/snackbar.service';
import { FileService } from '../../../shared/services/file-service/file.service';
import { error } from 'console';
import { CacheService } from '../../../shared/services/cache-service/cache.service';
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
    private snackBar: SnackBarService, private fileService: FileService, private cacheService: CacheService,
    private userService: UserService){
    this.loginResponse = {
      isError: false,
      data: null
    }
  }

  handleLoginData(formData: AuthRequestDTO): void {

    this.authService.login(formData).subscribe(
      {
        next: (data) => {
          console.log("uspelo")
          this.loginResponse = {isError: false, data: data as AuthResponseDTO}
         
          this.jwtService.login(data.token);
          // if(this.jwtService.hasRole(["SUPERADMIN"])) { 
          //   console.log("OVO JE SUPERADMIN")
          //   if(this.jwtService.isFirstSuperadminLogin()){
          //     console.log("MENJAMO SIFRU")
          //     this.router.navigate(['/auth/change-password']) 
          //     return;
          //   } 
          // }

          

          forkJoin({
            profileImg: this.fileService.getProfileImage(),
            partialUserData: this.userService.getPartialUserData()
          }).subscribe({
            next: ({profileImg, partialUserData}) => {
          
              this.cacheService.clear('userData');
              let imgUri: string = URL.createObjectURL(profileImg);
              this.cacheService.set('userData', [imgUri, partialUserData]);

              this.router.navigate(['']);
              this.snackBar.openSnackBar("Login successful")

            },
            error: (error) => {
              console.log("e ovde bas nismo")
              if(error.status == 400) this.loginResponse = {isError: true, error: error.error as ResponseMessage}
              console.log(this.loginResponse)
            }
          })

        

        },
        error: (error) => {
          
          if(error.status == 400) this.loginResponse = {isError: true, error: error.error as ResponseMessage}
     
        }
        
      }
    );

  }


}
