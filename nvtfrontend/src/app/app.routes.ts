import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'auth', 
        pathMatch: 'full', 
    },
    {
        path: 'auth',
        loadChildren: () => import('./auth/feature/auth-shell/auth-shell.module').then(m => m.AuthShellModule)
    },
    {
        path: '**', // ako ne skonta ni jedan route
        redirectTo: 'auth', // redirect na auth
    }
    
];
