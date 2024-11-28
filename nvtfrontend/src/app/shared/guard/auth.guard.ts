import { inject } from '@angular/core';
import { JwtService } from '../jwt-service/jwt.service';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const jwtService = inject(JwtService);
  const router = inject(Router);

  const requiredRole = route.data['role'] as string;

  if (jwtService.isLoggedIn() && jwtService.hasRole(requiredRole)) {
    return true;
  }else{
    router.navigate(["/auth/login"])
  }

  return false;
};