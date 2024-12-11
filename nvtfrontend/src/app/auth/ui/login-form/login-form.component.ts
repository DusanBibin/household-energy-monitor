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
  
      if(this.data.isError){
      
        this.loading = false;
        const error = this.data.error?.message || 'Unknown error';
     
        this.loginForm.controls['email'].setErrors({ backendError: error });
        if(!(error === "Email not confirmed for this user")) this.loginForm.controls['password'].setErrors({ backendError: error });

      }
    }

  }
  
  loginClick(): void {
  
    
    if(this.getControlError('email') === "Email not confirmed for this user") this.loginForm.get('email')?.setErrors(null)

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


  isControlInvalid(name: string): boolean{
    
    const control = this.loginForm.get(name);

    if(!control) {console.log("Control " + name + " doesn't exist"); return false;}
    return control.invalid;
  }

  getControl(name: string){
    return this.loginForm.get(name);
  }

  getControlError(name: string): string | null{
    const control = this.loginForm.get(name);
    
    if(!control) console.log("Control " + name + " doesn't exist");

    if(control?.hasError('required')) return 'This field is required';
    if(control?.hasError('backendError')) return control?.getError('backendError')
    
    return null;
  }


}
