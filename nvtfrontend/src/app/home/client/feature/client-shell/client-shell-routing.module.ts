import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [{
  path: '',
  redirectTo: 'vacant-households',
  pathMatch: 'full'
},
{
  path:'vacant-households',
  loadChildren: () =>
    import('../vacant-households/vacant-households.module').then(
      (m) => m.VacantHouseholdsModule
    )
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClientShellRoutingModule { }
