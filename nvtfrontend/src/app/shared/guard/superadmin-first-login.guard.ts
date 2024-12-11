import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { JwtService } from '../jwt-service/jwt.service';

export const superadminFirstLoginGuard: CanActivateFn = (route, state) => {
  const jwtService = inject(JwtService);
  const router = inject(Router);

  if (jwtService.isLoggedIn() && jwtService.hasRole(["SUPERADMIN"])) {

    if(jwtService.isFirstSuperadminLogin()) return true;
    else return false;
  
  }else{
    router.navigate(["/auth/login"])
  }

  return false;
};
