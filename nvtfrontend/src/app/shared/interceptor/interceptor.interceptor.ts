
import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { JwtService } from '../jwt-service/jwt.service';

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
  const jwtService = inject(JwtService); 
  const token = jwtService.getToken();

  if (token != null) {

    if(jwtService.isTokenExpired(token)){
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