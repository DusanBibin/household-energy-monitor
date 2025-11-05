import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClientRegistrationFormRoutingModule } from './client-registration-form-routing.module';
import { ClientRegistrationFormComponent } from './client-registration-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ImageCropperComponent } from 'ngx-image-cropper';


@NgModule({
  declarations: [ClientRegistrationFormComponent],
  imports: [
    CommonModule,
    ClientRegistrationFormRoutingModule,
    ReactiveFormsModule,
    ImageCropperComponent
  ],
  exports:[ClientRegistrationFormComponent]
})
export class ClientRegistrationFormModule { }
