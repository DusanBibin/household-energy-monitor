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
  path:'realestates',
  loadChildren: () =>
    import('../realestate-list/realestate-list.module').then(
      (m) => m.RealestateListModule
    )
},
{
  path:'household-requests',
  loadChildren:() => 
    import('../household-requests/household-requests.module').then(
      (m) => m.HouseholdRequestsModule
    )
},
{
  path:'realestate/:realestateId/household/:householdId/household-request/:requestId',
  loadChildren:() =>
    import('../household-request-details/household-request-details.module').then(
      (m) => m.HouseholdRequestDetailsModule
    )
},
{
  path:'realestate/:realestateId/household/:householdId',
  loadChildren:() =>
    import('../household-details/household-details.module').then(
      (m) => m.HouseholdDetailsModule
    )
},
{
  path:'appointments',
  loadChildren:() => 
    import('../client-appointments/client-appointments.module').then(
      (m) => m.ClientAppointmentsModule
    )
},
{
  path:'schedules/clerk/:clerkId',
  loadChildren:() =>
    import('../../../clerk/feature/schedules/schedules.module').then(
      (m) => m.SchedulesModule
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
