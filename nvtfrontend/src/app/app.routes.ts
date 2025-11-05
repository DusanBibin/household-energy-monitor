import { Routes } from '@angular/router';
import { authGuard } from './shared/guard/auth.guard';

export const routes: Routes = [
    {
        path: 'home',
        loadChildren: () => import('./home/home.module').then(m => m.HomeModule),
        canActivate: [authGuard],
        data: {roles: ['SUPERADMIN', 'ADMIN', 'CLIENT', 'CLERK']}
    },
    {
        path: 'auth',
        loadChildren: () => import('./auth/feature/auth-shell/auth-shell.module').then(m => m.AuthShellModule),
        canActivate: [authGuard],
        data: {roles: ['SUPERADMIN', 'ADMIN', 'CLIENT', 'CLERK']}
    },
    {
        path: '**', // ako ne skonta ni jedan route
        redirectTo: 'home', // redirect na ''
    }
    
];
