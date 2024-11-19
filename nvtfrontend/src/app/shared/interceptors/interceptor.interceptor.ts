
import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { JwtService } from '../jwt-service/jwt.service';

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
  console.log("1")
  const jwtService = inject(JwtService); 
  console.log("2")
  const token = jwtService.getToken();

  console.log("3")
  if (token != null) {

    if(jwtService.isTokenExpired(token)){
      jwtService.logout();
      console.log("4");
      
    }else{
      const cloned = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`),
      });
      console.log("5")
      return next(cloned);
    }
    
  }
  console.log("6")
  return next(req);
};