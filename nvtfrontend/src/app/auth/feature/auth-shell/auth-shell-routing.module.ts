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
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthShellRoutingModule { }
