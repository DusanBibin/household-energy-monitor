import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RealestateListDumbRoutingModule } from './realestate-list-dumb-routing.module';
import { RealestateListDumbComponent } from './realestate-list-dumb.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    RealestateListDumbComponent
  ],
  imports: [
    CommonModule,
    RealestateListDumbRoutingModule,
    FormsModule,
    ReactiveFormsModule
  ],
  exports: [RealestateListDumbComponent]
})
export class RealestateListDumbModule { }
