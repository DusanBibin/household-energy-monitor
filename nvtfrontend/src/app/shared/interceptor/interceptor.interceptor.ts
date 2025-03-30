
import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { JwtService } from '../services/jwt-service/jwt.service';
import { Router } from '@angular/router';
import { SnackBarService } from '../services/snackbar-service/snackbar.service';
import { Observable, throwError, catchError } from 'rxjs';


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
      router.navigate(['../auth/login'])
      snackBar.openSnackBar("Session has expired, please login again");
    }else{

      const cloned = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`),
      });
      return next(cloned);
    }
    
  }
  
  return next(req).pipe(
    catchError((error: any) => {
      if (error instanceof HttpErrorResponse) {
        if (!navigator.onLine) {
          // 🛑 No internet connection
          console.error('No Internet Connection');
          snackBar.openSnackBar('You are offline. Please check your internet connection.');
        } else if (error.status === 0) {
          // 🛑 Server unreachable (CORS issues, server down, etc.)
          console.error('Cannot reach the server');
          snackBar.openSnackBar('The server is unreachable. Please try again later.');
        } else if (error.status === 401) {
          // 🛑 Unauthorized (Token expired or invalid)
          console.error('Unauthorized - Logging out');
          jwtService.logout();
          router.navigate(['../auth/login']);
          snackBar.openSnackBar('Your session has expired. Please log in again.');
        } else if (error.status === 403) {
          // 🛑 Forbidden - No permission
          console.error('Forbidden - No access');
          snackBar.openSnackBar('You do not have permission to perform this action.');
        } else if (error.status >= 500) {
          // 🛑 Server error (Internal Server Error, etc.)
          console.error(`Server Error (${error.status}): ${error.message}`);
          snackBar.openSnackBar('Something went wrong on our end. Please try again later.');
        } else {
          // 🛑 General error handling
          console.error(`HTTP Error (${error.status}): ${error.message}`);
          snackBar.openSnackBar(error.error?.message || 'An unexpected error occurred.');
        }
      }

      return throwError(() => error);
    })
  );
};