import { Component } from '@angular/core';
import { SuperadminPasswordChangeDTO } from '../../data-access/model/auth-model'
import { AuthService } from '../../data-access/auth.service';
import { error } from 'console';
import { ResponseMessage, ResponseData } from '../../../shared/model';
import { Router } from '@angular/router';
import { SnackBarService } from '../../../shared/services/snackbar-service/snackbar.service';
import { JwtService } from '../../../shared/services/jwt-service/jwt.service';

@Component({
    selector: 'app-superadmin-change-password',
    templateUrl: './superadmin-change-password.component.html',
    styleUrl: './superadmin-change-password.component.css',
    standalone: false
})
export class SuperadminChangePasswordComponent {
  

  changePasswordResponse: ResponseData;
  constructor(private authService: AuthService, private router: Router, private snackBar: SnackBarService, private jwtService: JwtService){
    this.changePasswordResponse = {
      isError: false,
      data: null
    }
  }

  handleChangePasswordData(formData: SuperadminPasswordChangeDTO){

    this.authService.changeSuperadminPassword(formData).subscribe(
      {
        next: (data) => {
          this.changePasswordResponse = {isError: false, data: data as ResponseMessage};



          this.authService.logout().subscribe({
            next: value => {
              this.jwtService.setUser(null);
              this.router.navigate(["/auth/login"])
              this.snackBar.openSnackBar("Password change successful")
            },
            error: error => {
              console.log(error)
            }
          })
         
        },
        error: (error) => {
          
          if([400, 401, 403].includes(error.status)) this.changePasswordResponse = {isError: true, error: error.error as ResponseMessage};
     
        }
        
      }
    );
  
  }

}
