import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
{
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
},
{
  path:'realestate/:id',
  loadChildren:() =>
    import('../household-details/household-details.module').then(
      (m) => m.HouseholdDetailsModule
    )

},
{
  path: '**', // ako ne skonta ni jedan route
  redirectTo: 'vacant-households', // redirect na ''
}


];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClientShellRoutingModule { }
