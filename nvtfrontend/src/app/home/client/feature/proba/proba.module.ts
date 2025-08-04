import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProbaRoutingModule } from './proba-routing.module';
import { ProbaComponent } from './proba.component';
import { NgxEchartsModule, provideEchartsCore } from 'ngx-echarts';

@NgModule({
  declarations: [ProbaComponent],
  imports: [
    CommonModule,
    ProbaRoutingModule,
    NgxEchartsModule
  ],
  providers: [
    provideEchartsCore({
      echarts: () => import('echarts')
    })
  ]
})
export class ProbaModule { }
