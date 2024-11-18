import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

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
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthShellRoutingModule { }
