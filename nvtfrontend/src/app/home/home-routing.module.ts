import { NgModule, inject } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home.component';
import { JwtService } from '../shared/services/jwt-service/jwt.service';
import { roleGuard } from '../shared/guard/role.guard';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    // canActivate:[roleGuard],
    // data: {roles: ['ADMIN', 'SUPERADMIN', 'CLIENT', 'OFFICIAL']},
    children: [
      
      {
        path: '',
        redirectTo: () => {
          const jwtService = inject(JwtService);
          
          if(jwtService.hasRole(['SUPERADMIN'])) return 'superadmin'
          if(jwtService.hasRole(['CLIENT'])) return 'client'
          return ''
        },
        pathMatch: 'full'
      },
      {
        path: 'superadmin',
        loadChildren: () =>
          import('./superadmin/feature/superadmin-shell/superadmin-shell-routing.module').then(
            (m) => m.SuperadminShellRoutingModule
          ),
          canActivate: [roleGuard],
          data: {roles: ['SUPERADMIN']}
      },
      {
        path:'client',
        loadChildren: () =>
          import('./client/feature/client-shell/client-shell-routing.module').then(
            (m) => m.ClientShellRoutingModule
          ),
          canActivate: [roleGuard],
          data: {roles: ['CLIENT']}
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
