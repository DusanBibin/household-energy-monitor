import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  
  {
    path:'schedules',
    loadChildren:() =>
      import('../../../clerk/feature/schedules/schedules.module').then(
        (m) => m.SchedulesModule
      )
  
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClerkShellRoutingModule { }
