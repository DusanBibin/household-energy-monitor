import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../data-access/auth.service';
import { ResponseData, ResponseMessage } from '../../../shared/model';
import { error } from 'console';
import { Router } from '@angular/router';
import { SnackBarService } from '../../../shared/services/snackbar-service/snackbar.service';
import { RegisterRequestDTO } from '../../data-access/model/auth-model';
import { JwtService } from '../../../shared/services/jwt-service/jwt.service';

@Component({
    selector: 'app-client-registration',
    templateUrl: './client-registration.component.html',
    styleUrl: './client-registration.component.css',
    standalone: false
})
export class ClientRegistrationComponent {

  registerResponse: ResponseData;
  constructor(private authService: AuthService, private router: Router, private snackBar: SnackBarService, private jwtService: JwtService){
    this.registerResponse = {
      isError: false,
      data: null
    }
  }

  handleRegisterData(event: {formData: FormData, email: string}){
    this.jwtService.logout();
    this.authService.registerClient(event.formData).subscribe({
      next: (data) =>{
        this.registerResponse = {isError: false, data: data as ResponseMessage}
        this.router.navigate(["/auth/login"])

        this.snackBar.openSnackBar("Confirmation email sent to " + event.email)
        console.log("I need the succsezzz")
      },
      error: (error) =>{
        console.log("z virus daym ")
        if(error.status == 400) this.registerResponse = {isError: true, error: error.error as ResponseMessage}
      }
    })

  }

  
}
