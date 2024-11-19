import { Component, ViewEncapsulation } from '@angular/core';
import { AuthRequestDTO, AuthResponseDTO } from '../../data-access/model/auth-model';
import { AuthService } from '../../data-access/auth.service';
import { ErrorMessage, ResponseData } from '../../../shared/model';
import { JwtService } from '../../../shared/jwt-service/jwt.service';

@Component({
  selector: 'app-auth',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  loginResponse: ResponseData;
  constructor(private authService: AuthService, private jwtService: JwtService){
    this.loginResponse = {
      isError: false,
      data: null
    }
  }

  handleLoginData(formData: AuthRequestDTO): void {

    this.authService.login(formData).subscribe(
      {
        next: (data) => {
          console.log(data)
          this.loginResponse = {isError: false, data: data as AuthResponseDTO}
          console.log(this.loginResponse)
          this.jwtService.login(data.token);
          
        },
        error: (error) => {
          
          console.log(error)

          if(error.status == 400){
            console.log(this.loginResponse)
            this.loginResponse = {isError: true, error: error.error as ErrorMessage}
            console.log(this.loginResponse)
          }
          
        }
        
      }
    );

  }

}
