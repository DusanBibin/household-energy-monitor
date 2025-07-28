import { NgModule, inject } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home.component';
import { JwtService } from '../shared/services/jwt-service/jwt.service';
import { roleGuard } from '../shared/guard/role.guard';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    children: [
      {
        path: 'superadmin',
        canActivate: [roleGuard],
        data: { roles: ['SUPERADMIN'] },
        loadChildren: () =>
          import('./superadmin/feature/superadmin-shell/superadmin-shell-routing.module').then(
            (m) => m.SuperadminShellRoutingModule
          )
      },
      {
        path:'client',
        canActivate: [roleGuard],
        data: { roles: ['CLIENT'] },
        loadChildren: () =>
          import('./client/feature/client-shell/client-shell-routing.module').then(
            (m) => m.ClientShellRoutingModule
          )
      },
      {
        path:'admin',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
        loadChildren: () =>
          import('./admin/feature/admin-shell/admin-shell-routing.module').then(
            (m) => m.AdminShellRoutingModule
          )
      }
    ]
  }
  
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HomeRoutingModule {
 }
