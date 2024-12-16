import { Component } from '@angular/core';
import { SuperadminPasswordChangeDTO } from '../../data-access/model/auth-model'
import { AuthService } from '../../data-access/auth.service';
import { error } from 'console';
import { ResponseMessage, ResponseData } from '../../../shared/model';
import { Router } from '@angular/router';
import { SnackBarService } from '../../../shared/services/snackbar-service/snackbar.service';

@Component({
  selector: 'app-superadmin-change-password',
  templateUrl: './superadmin-change-password.component.html',
  styleUrl: './superadmin-change-password.component.css'
})
export class SuperadminChangePasswordComponent {
  

  changePasswordResponse: ResponseData;
  constructor(private authService: AuthService, private router: Router, private snackBar: SnackBarService){
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
          this.router.navigate(["/auth/login"])
          
          this.snackBar.openSnackBar("Password change successful")
        },
        error: (error) => {
          
          if(error.status == 400) this.changePasswordResponse = {isError: true, error: error.error as ResponseMessage};
     
        }
        
      }
    );
  
  }

}
