import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'appointments',
    pathMatch: 'full'
  },
  {
    path:'appointments',
    loadChildren:() => 
      import('../../../client/feature/client-appointments/client-appointments.module').then(
        (m) => m.ClientAppointmentsModule
      )
  },
  {
    path: '**', // ako ne skonta ni jedan route
    redirectTo: 'appointments', // redirect na ''
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClerkShellRoutingModule { }
