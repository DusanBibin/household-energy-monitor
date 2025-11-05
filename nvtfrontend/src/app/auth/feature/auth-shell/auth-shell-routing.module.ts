import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { superadminFirstLoginGuard } from '../../../shared/guard/superadmin-first-login.guard';

const routes: Routes = [
  {
    path: '', // samo doda /login na /auth ako je /auth
    redirectTo: 'login', 
    pathMatch: 'full', 
  },
  {
    path: 'login',
    loadChildren: () =>
      import('../login/login.module').then(
        (m) => m.LoginModule
      ),
  },
  {
    path:'change-password',
    loadChildren: () =>
      import('../superadmin-change-password/superadmin-change-password.module').then(
        (m) => m.SuperadminChangePasswordModule
      ),
    canActivate: [superadminFirstLoginGuard]
  },
  {
    path:'registration',
    loadChildren: () =>
      import('../client-registration/client-registration.module').then(
        (m) => m.ClientRegistrationModule
      )
  },{
    path:'verification/:verificationCode',
    loadChildren: () =>
      import('../verification/verification.module').then(
        (m) => m.VerificationModule
      )
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthShellRoutingModule { }
