import { Component, Input, Output, SimpleChanges, ViewChild, TemplateRef} from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { passwordsMatchValidator, passwordValidator } from '../../../shared/custom-validators';
import { RegisterRequestDTO } from '../../data-access/model/auth-model';
import { ResponseData } from '../../../shared/model';
import { EventEmitter } from '@angular/core'
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { ImageCroppedEvent, LoadedImage } from 'ngx-image-cropper';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { JwtService } from '../../../shared/services/jwt-service/jwt.service';

@Component({
    selector: 'app-client-registration-form',
    templateUrl: './client-registration-form.component.html',
    styleUrl: './client-registration-form.component.css',
    standalone: false
})
export class ClientRegistrationFormComponent{

  loading: boolean = false;
  registerClicked: boolean = false;

  @Input() data: ResponseData;
  @Output() registerSent = new EventEmitter<{formData: FormData, email: string}>();

  registrationForm: FormGroup





  @ViewChild('fileInput') fileInput: any;
  profileImg: File | null = null;
  imgErrMsg: string = "";
  isInvalidImg: boolean = false;
  //imgUrl: string | ArrayBuffer | null = null;
  imgUrl: SafeUrl = '';
  croppedImgUrl: SafeUrl = '';
  minCrop: number = 0;

  imageChangedEvent: Event | null = null;
  @ViewChild('cropDialog') cropDialog: TemplateRef<any> | null = null;

  
  constructor(private fb: FormBuilder, private modalService: NgbModal, private sanitizer: DomSanitizer, protected jwtService: JwtService){
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


  ngOnChanges(changes: SimpleChanges): void{
    if(changes['data'] && this.registerClicked){

      if(this.data.isError){

        this.loading = false;
        const error = this.data.error?.message || 'Unknown error';

        this.registrationForm.controls['email'].setErrors({backendError: error});
      }else{
        this.profileImg = null;
        this.imgUrl = "";
        this.croppedImgUrl = "";
        this.registrationForm.reset();
        this.loading = false;
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
    
    const fileInput = event.target as HTMLInputElement;

    if (fileInput?.files && fileInput.files.length > 0) {
      const file = fileInput.files[0];
    
      // Check file type
      if (!(file.type === 'image/jpeg' || file.type === 'image/png')) {
        this.isInvalidImg = true;
        this.imgErrMsg = 'Profile image can only be png or jpg';
      } else {
        this.isInvalidImg = false;
    
        // Use FileReader to read the image
        const reader = new FileReader();
        reader.onload = (e: any) => {
          const img = new Image();
          img.src = e.target.result;
    
          img.onload = () => {
            const width = img.width;
            const height = img.height;
            
            console.log(`Image dimensions: ${width}x${height}`);
            
            // Perform actions based on dimensions if needed
            if (width < 128 || height < 128) {
              this.isInvalidImg = true;
              this.imgErrMsg = 'Image is too small';
            } else {

              this.imageChangedEvent = event;
              
              this.minCrop = width < height ? width : height;
              if (this.cropDialog) {
                this.modalService.open(this.cropDialog, {
                  centered: true,
                  scrollable: true,
                  backdrop: 'static',
                });
              } else {
                console.error('Crop dialog template is not defined.');
              }
            }
          };
    
          img.onerror = () => {
            console.error('Invalid image file.');
            this.isInvalidImg = true;
            this.imgErrMsg = 'Unable to process the selected image.';
          };
        };
    
        reader.readAsDataURL(file);
      }
    }
    
    
  }

  imageCropped(event: ImageCroppedEvent) {
    if (event.objectUrl) {
      this.croppedImgUrl = this.sanitizer.bypassSecurityTrustUrl(event.objectUrl as string);
      if (event.blob) {
        this.profileImg = new File([event.blob], 'cropped-image.png', { type: 'image/png' });
        console.log('Cropped Image File:', this.profileImg);
      } else {
        console.error('Image cropping failed: No Blob received.');
      }
    } else {
      console.error('Invalid objectUrl from cropped event.');
    }
    
  }

  cropImage(){
    console.log("iksdebro1")
    if (this.cropDialog) {
      this.imgUrl = this.croppedImgUrl;
      this.modalService.dismissAll(); 
    }
  }

  imageLoaded(image: LoadedImage) {}

  cropperReady() {}

  loadImageFailed() {}
  





  //error handling frontend
  isControlInvalid(name: string): boolean{
    const control = this.registrationForm.get(name);
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
