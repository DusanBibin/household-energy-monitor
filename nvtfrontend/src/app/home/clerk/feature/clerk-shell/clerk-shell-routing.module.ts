import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path:'appointments',
    loadChildren:() => 
      import('../../../client/feature/client-appointments/client-appointments.module').then(
        (m) => m.ClientAppointmentsModule
      )
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClerkShellRoutingModule { }
