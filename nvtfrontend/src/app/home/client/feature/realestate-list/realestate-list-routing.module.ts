import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RealestateListComponent } from './realestate-list.component';

const routes: Routes = [{
  path: '',
  component: RealestateListComponent 
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RealestateListRoutingModule { }
