import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { VacantHouseholdsComponent } from './vacant-households.component';

const routes: Routes = [
  {
      path: '',
      component: VacantHouseholdsComponent,
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class VacantHouseholdsRoutingModule { }
