import { Component, OnInit, Output, EventEmitter, Input, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { passwordsMatchValidator, passwordValidator } from '../../../shared/custom-validators';
import { SuperadminPasswordChangeDTO } from '../../data-access/model/auth-model'

@Component({
  selector: 'app-superadmin-change-password-form',
  templateUrl: './superadmin-change-password-form.component.html',
  styleUrl: './superadmin-change-password-form.component.css'
})
export class SuperadminChangePasswordFormComponent implements OnInit{
    
  @Output() changePasswordSent = new EventEmitter<SuperadminPasswordChangeDTO>();

  changePasswordForm: FormGroup;
  loading: boolean = false;
  changePasswordClicked: boolean = false;

  constructor(private fb: FormBuilder){

    this.changePasswordForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(10), passwordValidator]],
      repeatPassword: ['', [Validators.required]]
    }, {validators: passwordsMatchValidator})

    this.changePasswordForm.valueChanges.subscribe(() => {
        if(this.changePasswordClicked) this.changePasswordClicked = false;
        if(this.changePasswordForm.get('email')?.hasError('backendError')) this.changePasswordForm.get('email')?.setErrors(null)
        if(this.changePasswordForm.get('password')?.hasError('backendError')) this.changePasswordForm.get('password')?.setErrors(null)
        
      }
    )

  }

  changePassword(): void{
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

  getNewPasswordError(): string | null {
    const newPassword = this.newPassword


    if(newPassword?.hasError('required')){
      return 'This field is required'
    }

    if(newPassword?.hasError('minlength')){
      return 'Password must be at least 10 characters long';
    }

    if(newPassword?.hasError('passwordStrength')){
      return newPassword.getError('passwordStrength')
    }

    if(this.changePasswordForm.hasError('passwordsMismatch')){
      return this.changePasswordForm.getError('passwordsMismatch')
    }

    return null;
  }

  getRepeatPasswordError(): string | null {
    const repeatPassword = this.repeatPassword

    if(repeatPassword?.hasError('required')){
      return 'This field is required'
    }


    if(this.changePasswordForm.hasError('passwordsMismatch')){
      return this.changePasswordForm.getError('passwordsMismatch')
    }

    return null;
  }

  ngOnInit(): void {
      
  }

  

}
