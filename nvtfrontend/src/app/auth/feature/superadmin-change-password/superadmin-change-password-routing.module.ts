import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SuperadminChangePasswordComponent } from './superadmin-change-password.component';

const routes: Routes = [
  {
    path:'',
    component: SuperadminChangePasswordComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SuperadminChangePasswordRoutingModule { }
