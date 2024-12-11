import { Component, ViewEncapsulation } from '@angular/core';
import { AuthRequestDTO, AuthResponseDTO } from '../../data-access/model/auth-model';
import { AuthService } from '../../data-access/auth.service';
import { ResponseMessage, ResponseData } from '../../../shared/model';
import { JwtService } from '../../../shared/jwt-service/jwt.service';
import { Router } from '@angular/router';
import { SnackBarService } from '../../../shared/snackbar-service/snackbar.service';

@Component({
  selector: 'app-auth',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  loginResponse: ResponseData;
  constructor(private router: Router, private authService: AuthService, private jwtService: JwtService, private snackBar: SnackBarService){
    this.loginResponse = {
      isError: false,
      data: null
    }
  }

  handleLoginData(formData: AuthRequestDTO): void {

    this.authService.login(formData).subscribe(
      {
        next: (data) => {
       
          this.loginResponse = {isError: false, data: data as AuthResponseDTO}
         
          this.jwtService.login(data.token);
          if(this.jwtService.hasRole(["SUPERADMIN"])){

            if(this.jwtService.isFirstSuperadminLogin()) this.router.navigate(['/auth/change-password'])
            
          }
          
          this.router.navigate(['']);
          this.snackBar.openSnackBar("Login successful")
        },
        error: (error) => {
          
          if(error.status == 400) this.loginResponse = {isError: true, error: error.error as ResponseMessage}
     
        }
        
      }
    );

  }

}
