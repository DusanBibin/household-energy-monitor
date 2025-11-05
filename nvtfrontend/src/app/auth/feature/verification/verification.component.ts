import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../data-access/auth.service';
import { error } from 'console';
import { ResponseData, ResponseMessage } from '../../../shared/model';


@Component({
  selector: 'app-verification',
  standalone: false,
  
  templateUrl: './verification.component.html',
  styleUrl: './verification.component.css'
})
export class VerificationComponent implements OnInit{

  validationCode: string= "";

  response: ResponseData | null = null;
  constructor(private route: ActivatedRoute, private authService: AuthService){}

  ngOnInit(): void {


    this.route.paramMap.subscribe(params => {
      if(params.get('verificationCode')){
        let code = params.get('verificationCode');
        
        if(code){
          this.validationCode = code;
          this.authService.verifyAccount(this.validationCode).subscribe({
            next: email =>  {
              console.log(email)
              this.response = {isError: false, data: email}
              console.log(this.response)
            },error: error =>{
              this.response = {isError: true, error: error.error as ResponseMessage}
              console.log(this.response)
            }
          })
        }

        
        
      
      }
    });
  }

}
