import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RealestateListRoutingModule } from './realestate-list-routing.module';
import { RealestateListComponent } from './realestate-list.component';
import { RealestateListDumbModule } from '../../ui/realestate-list-dumb/realestate-list-dumb.module';


@NgModule({
  declarations: [
    RealestateListComponent
  ],
  imports: [
    CommonModule,
    RealestateListRoutingModule,
    RealestateListDumbModule
  ]
})
export class RealestateListModule { }
