import { Component, OnInit, Output, EventEmitter, Input, SimpleChanges } from '@angular/core';
import { FormGroup, FormBuilder, Validators} from '@angular/forms';
import { AuthRequestDTO } from '../../data-access/model/auth-model'
import { ErrorMessage, ResponseData } from '../../../shared/model';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.css'
})
export class LoginFormComponent implements OnInit {
  
  @Input() data: ResponseData;
  
  @Output() loginSent = new EventEmitter<AuthRequestDTO>();
  loginForm: FormGroup;
  loading: boolean = false;
  loginClicked: boolean = false;
  
  constructor(private fb: FormBuilder){

    this.data = {isError: false}
    this.loginForm = this.fb.group({
      email: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });


    this.loginForm.valueChanges.subscribe(() => {
      if (this.loginClicked) {
        this.loginClicked = false;
      }
    });

  }

  ngOnInit(): void {}

  ngOnChanges(changes: SimpleChanges): void {

    if (changes['data'] && this.loginClicked) {
      if(this.data.isError){
        this.loading = false;
        const error = this.data.error?.message || 'Unknown error';
        console.log("greska je " + error)
        this.loginForm.controls['email'].setErrors({ backendError: error });
        if(error === "Email or password is invalid") this.loginForm.controls['password'].setErrors({ backendError: error });

      }
    }

  }
  
  loginClick(): void {
    this.data.isError = false;
    this.loginClicked = true;
    this.loading = true;
  
    if(this.loginForm.invalid){
      this.loading = false;
      return;
    }
    let formData: AuthRequestDTO = this.loginForm.value;

    this.loginSent.emit(formData);
  }

}
