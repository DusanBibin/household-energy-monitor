import { inject } from '@angular/core';
import { JwtService } from '../services/jwt-service/jwt.service';
import { CanActivateFn, Router } from '@angular/router';
import { SnackBarService } from '../services/snackbar-service/snackbar.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const isBrowser = (): boolean => typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  const jwtService = inject(JwtService);
  const router = inject(Router);
  const snackBar = inject(SnackBarService);
  const allowedRoles = route.data['roles'] as string[];
  
  const allowedRole = allowedRoles[0]
  const currentPath = route.url[0].path;

  console.log(allowedRole);
  console.log(currentPath)
  console.log(jwtService.hasRole([allowedRole]))

  if(!(currentPath.includes(allowedRole.toLowerCase()) && jwtService.hasRole([allowedRole]))){
    router.navigate(['/home'], {replaceUrl: true})
    return false;
  }


  if (!isBrowser()) {
    console.log("Not running in a browser environment.");
    return false; 
  };



  return true;
};
