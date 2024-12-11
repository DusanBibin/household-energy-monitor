import { inject } from '@angular/core';
import { JwtService } from '../jwt-service/jwt.service';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const isBrowser = (): boolean => typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  const jwtService = inject(JwtService);
  const router = inject(Router);

  const allowedRoles = route.data['roles'] as string[];

  console.log(jwtService.isLoggedIn())
  console.log(jwtService.hasRole(allowedRoles))
  if (jwtService.isLoggedIn() && jwtService.hasRole(allowedRoles)) {
    console.log("upravu sii")
    return true;
  }

  if (!isBrowser()) {
    console.log("Not running in a browser environment.");
    return false; 
  }

  console.log("nisi upravu")
  router.navigate(['/auth/login']);
  return false;
};