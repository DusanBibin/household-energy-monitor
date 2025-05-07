import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'admin-registration', // Redirect to the default route
    pathMatch: 'full' // Ensures it only redirects on the empty path
  },
  {
    path: 'admin-registration',
    loadChildren: () =>
      import('../../../../auth/feature/client-registration/client-registration.module').then(
        (m) => m.ClientRegistrationModule
      )
  },
  {
    path: '**', // ako ne skonta ni jedan route
    redirectTo: 'admin-registration', // redirect na ''
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SuperadminShellRoutingModule { }
