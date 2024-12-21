import { AfterViewInit, Component, Input, OnInit, Output, SimpleChanges, ViewChild, ViewChildren, Inject, PLATFORM_ID, ElementRef} from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { passwordsMatchValidator, passwordValidator } from '../../../shared/custom-validators';
import { RegisterRequestDTO } from '../../data-access/model/auth-model';
import { ResponseData } from '../../../shared/model';
import { EventEmitter } from '@angular/core'
import { Modal } from 'bootstrap';
import { isPlatformBrowser} from '@angular/common';
import { After } from 'v8';

@Component({
  selector: 'app-client-registration-form',
  templateUrl: './client-registration-form.component.html',
  styleUrl: './client-registration-form.component.css'
})
export class ClientRegistrationFormComponent implements AfterViewInit{

  @ViewChild('cropDialog', { static: true }) cropDialog!: ElementRef;
  private modalInstance!: Modal;

  @Input() data: ResponseData;
  @Output() registerSent = new EventEmitter<{formData: FormData, email: string}>();

  registrationForm: FormGroup

  @ViewChild('fileInput') fileInput: any;
  profileImg: File | null = null;
  imgErrMsg: string = "";
  isInvalidImg: boolean = false;
  imgUrl: string | ArrayBuffer | null = null;


  loading: boolean = false;
  registerClicked: boolean = false;

  constructor(private fb: FormBuilder, @Inject(PLATFORM_ID) private platformId: object ){
    console.log(this.profileImg)

    this.data = {isError: false}
    this.registrationForm = this.fb.group({
      name: ['', [Validators.required]],
      lastname: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^06\d{8,9}$/)]],
      password: ['', [Validators.required, Validators.minLength(10), passwordValidator]],
      repeatPassword: ['', [Validators.required]]
    }, {validators: passwordsMatchValidator('password', 'repeatPassword')})

    this.registrationForm.valueChanges.subscribe(() =>{
      if(this.registerClicked) this.registerClicked = false;
      if(this.registrationForm.get('email')?.hasError('backendError')) this.registrationForm.get('newPassword')?.setErrors(null)

    })

  }

  ngAfterViewInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.modalInstance = new Modal(this.cropDialog.nativeElement);
    }
  }


  ngOnChanges(changes: SimpleChanges): void{
    if(changes['data'] && this.registerClicked){

      if(this.data.isError){

        this.loading = false;
        const error = this.data.error?.message || 'Unknown error';

        this.registrationForm.controls['email'].setErrors({backendError: error});
      }

    }
  }

  registerClick(): void{

    console.log("da li radi ovo")
    this.registerClicked = true;
    this.loading = true;

    if(!this.profileImg) { this.isInvalidImg = true; this.imgErrMsg = "This field is required"}

    if(this.registrationForm.invalid || this.isInvalidImg){
      console.log(this.registrationForm.errors)
      this.loading = false;
      console.log("verovatno ne lol")
      return;
    }

    console.log("jej radi")

    let formData = new FormData();
    let registerData: RegisterRequestDTO = this.registrationForm.value;

    console.log(registerData)


    formData.append('formData',  new Blob([JSON.stringify(registerData)], { type: 'application/json' }))
    if(this.profileImg) formData.append('profileImage', this.profileImg)
    
    const email = registerData.email
    this.registerSent.emit({formData, email});
  }

  onImagePicked(event: Event): void {
    console.log("OVO SE UPALILO")
    const fileInput = event.target as HTMLInputElement;
    
  
    if (fileInput?.files && fileInput.files.length > 0) {
      console.log(fileInput.files[0].type)
      if(!(fileInput.files[0].type === 'image/jpeg' || fileInput.files[0].type === 'image/png')){ 
        this.isInvalidImg = true;
        this.imgErrMsg = "Profile image can only be png or jpg"
      }
      else { this.isInvalidImg = false;
        const reader = new FileReader();

        reader.onload = () => {
          this.imgUrl = reader.result; // Set the image URL
        };

        reader.readAsDataURL(fileInput.files[0]);

        if (this.modalInstance) this.modalInstance.show();
        console.log("iksde")
      }
      this.profileImg = fileInput.files[0]

      


    }
    
  }

  isControlInvalid(name: string): boolean{
    console.log("da li smo usli ovde uopste?")
    console.log("Jebem ti mamu")
    const control = this.registrationForm.get(name);
    console.log(control?.valid)
    if(!control) {console.log("Control " + name + " doesn't exist"); return false;}
    
    if(name === 'password' || name === 'repeatPassword') return control.invalid || this.registrationForm.hasError('passwordsMismatch');
    else return control.invalid;
  }



  getControl(name: string){
    return this.registrationForm.get(name);
  }


  getControlError(name: string): string | null{
    console.log("usli smo ovde")
    const control = this.registrationForm.get(name);
    
    if(!control) console.log("Control " + name + " doesn't exist");

    if(control?.hasError('required')) return 'This field is required';
    if(control?.hasError('minlength')) return 'Password must be at least 10 characters long';
    if(control?.hasError('email')) return 'Email is invalid';
    if(control?.hasError('pattern')) return 'Phone number is invalid';
    if(control?.hasError('passwordStrength')) return control.getError('passwordStrength');
    if((name === 'password' || name === 'repeatPassword') && this.registrationForm.hasError('passwordsMismatch')) 
      return this.registrationForm.getError('passwordsMismatch');
    if(control?.hasError('backendError')) return control?.getError('backendError')
    
    console.log("ovo nije dobro xd")
    return null;
  }

  // resetFileInput(): void {
  //   if (this.fileInput) {
  //     this.fileInput.nativeElement.value = '';

  //   }
  // }

  // ngAfterViewInit(){
  //   this.resetFileInput
  // }


}
