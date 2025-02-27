
import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { JwtService } from '../services/jwt-service/jwt.service';
import { Router } from '@angular/router';
import { SnackBarService } from '../services/snackbar-service/snackbar.service';


export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
  const jwtService = inject(JwtService); 
  const snackBar = inject (SnackBarService)
  const token = jwtService.getToken();
  const router = inject(Router);

  if (token != null) {
    console.log("imamo token")
    if(jwtService.isTokenExpired(token)){
      console.log("istekao je")
      jwtService.logout();
      
    }else{

      const cloned = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`),
      });
      return next(cloned);
    }
    
  }
  
  return next(req);
};