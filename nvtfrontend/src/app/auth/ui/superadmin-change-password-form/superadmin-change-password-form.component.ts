import { Component, OnInit, Output, EventEmitter, Input, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { passwordsMatchValidator, passwordValidator } from '../../../shared/custom-validators';
import { SuperadminPasswordChangeDTO } from '../../data-access/model/auth-model'
import { ResponseMessage, ResponseData } from '../../../shared/model';

@Component({
    selector: 'app-superadmin-change-password-form',
    templateUrl: './superadmin-change-password-form.component.html',
    styleUrl: './superadmin-change-password-form.component.css',
    standalone: false
})
export class SuperadminChangePasswordFormComponent implements OnInit{
    

  @Input() data: ResponseData;
  @Output() changePasswordSent = new EventEmitter<SuperadminPasswordChangeDTO>();

  changePasswordForm: FormGroup;
  loading: boolean = false;
  changePasswordClicked: boolean = false;

  constructor(private fb: FormBuilder){

    this.data = {isError: false}
    this.changePasswordForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(10), passwordValidator]],
      repeatPassword: ['', [Validators.required]]
    }, {validators: passwordsMatchValidator('newPassword', 'repeatPassword')})

    this.changePasswordForm.valueChanges.subscribe(() => {
        if(this.changePasswordClicked) this.changePasswordClicked = false;
        if(this.changePasswordForm.get('newPassword')?.hasError('backendError')) this.changePasswordForm.get('newPassword')?.setErrors(null)
        if(this.changePasswordForm.get('repeatPassword')?.hasError('backendError')) this.changePasswordForm.get('repeatPassword')?.setErrors(null)
        
      }
    )

  }

  ngOnChanges(changes: SimpleChanges): void {
 
    if (changes['data'] && this.changePasswordClicked) {
  
      if(this.data.isError){
      
        this.loading = false;
        const error = this.data.error?.message || 'Unknown error';
     
        this.changePasswordForm.controls['newPassword'].setErrors({ backendError: error });
        if(error === "Email or password is invalid") this.changePasswordForm.controls['repeatPassword'].setErrors({ backendError: error });

      }
    }

  }

  changePasswordClick(): void{
    this.changePasswordClicked = true;
    this.loading = true;
   
    if(this.changePasswordForm.invalid){
      this.loading = false;
      return;
    }
  

    let formData: SuperadminPasswordChangeDTO = this.changePasswordForm.value;

    this.changePasswordSent.emit(formData)

  }

  get newPassword() {
    return this.changePasswordForm.get('newPassword');
  }

  get repeatPassword() {
    return this.changePasswordForm.get('repeatPassword');
  }

  isControlInvalid(name: string): boolean{
    const control = this.changePasswordForm.get(name);
    console.log(control?.valid)
    if(!control) {console.log("Control " + name + " doesn't exist"); return false;}
    
    if(name === 'newPassword' || name === 'repeatPassword') return control.invalid || this.changePasswordForm.hasError('passwordsMismatch');
    else return control.invalid;
  }

  getControl(name: string){
    return this.changePasswordForm.get(name);
  }

  getControlError(name: string): string | null{
    const control = this.changePasswordForm.get(name);

    if(!control) console.log("Control " + name + " doesn't exist");
    
    if(control?.hasError('required')) return 'This field is required';
    if(control?.hasError('minlength')) return 'Password must be at least 10 characters long';
    if(control?.hasError('passwordStrength')) return control.getError('passwordStrength')
    if((name === 'newPassword' || name === 'repeatPassword') && this.changePasswordForm.hasError('passwordsMismatch')) 
      return this.changePasswordForm.getError('passwordsMismatch');
    if(control?.hasError('backendError')) return control?.getError('backendError')
    
    return null;
  }

  ngOnInit(): void {
      
  }

  

}
