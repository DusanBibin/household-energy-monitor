import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    children: [
      {
        path: 'admin-registration',
        loadChildren: () =>
          import('../auth/feature/client-registration/client-registration.module').then(
            (m) => m.ClientRegistrationModule
          )
      },
      {
        path:'vacant-households-client',
        loadChildren: () =>
          import('./feature/vacant-households-client/vacant-households-client.module').then(
            (m) => m.VacantHouseholdsClientModule
          )
      }
    ]
  }
  
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HomeRoutingModule { }
