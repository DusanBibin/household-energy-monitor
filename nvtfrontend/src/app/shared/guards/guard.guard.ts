import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, CanActivate} from '@angular/router';
import { JwtService } from '../jwt-service/jwt.service';


@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private jwtService: JwtService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const requiredRole = route.data['role'] as string;

    if (this.jwtService.isLoggedIn() && this.jwtService.hasRole(requiredRole)) {
      return true;
    }

    return false;
  }
}