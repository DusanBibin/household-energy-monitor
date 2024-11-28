import { Component, OnInit, Output, EventEmitter, Input, SimpleChanges } from '@angular/core';
import { FormGroup, FormBuilder, Validators} from '@angular/forms';
import { AuthRequestDTO } from '../../data-access/model/auth-model'
import { ResponseMessage, ResponseData } from '../../../shared/model';

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

    //reaguje na promene inputa 
    this.loginForm.valueChanges.subscribe(() => {
      if (this.loginClicked) this.loginClicked = false;

      if(this.loginForm.get('email')?.hasError('backendError')) this.loginForm.get('email')?.setErrors(null)
      if(this.loginForm.get('password')?.hasError('backendError')) this.loginForm.get('password')?.setErrors(null)
    });

  }

  ngOnInit(): void {}

 
  //reaguje na promene responsa
  ngOnChanges(changes: SimpleChanges): void {
 
    if (changes['data'] && this.loginClicked) {
      console.log("1")
      if(this.data.isError){
        console.log("2")
        this.loading = false;
        const error = this.data.error?.message || 'Unknown error';
     
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

  get email(){
    return this.loginForm.get('email');
  }

  get password(){
    return this.loginForm.get('password');
  }

  getEmailError(): string | null {
    const email = this.email
    
    if(email?.hasError('required')){
      return 'This field is required'
    }

    if(email?.hasError('backendError')){
      return email?.getError('backendError')
    }

    return null;
  }

  getPasswordError(): string | null {
    const password = this.password

    if(password?.hasError('required')){
      return 'This field is required'
    }

    if(password?.hasError('backendError')){
      return password?.getError('backendError')
    }

    return null;
  }

}
