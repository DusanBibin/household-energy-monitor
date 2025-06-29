
import { inject, NgZone } from '@angular/core';
import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { JwtService } from '../services/jwt-service/jwt.service';
import { Router } from '@angular/router';
import { SnackBarService } from '../services/snackbar-service/snackbar.service';
import { Observable, throwError, catchError } from 'rxjs';
import { AuthService } from '../../auth/data-access/auth.service';


export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
  const snackBar = inject(SnackBarService);
  const router = inject(Router);
  const authService = inject(AuthService)
  const zone = inject(NgZone);
  const jwtService = inject(JwtService);
  // Clone the request to ensure cookies are sent
  const clonedRequest = req.clone({ withCredentials: true });
  const isAuthRequest = req.url.includes('/auth/');


  return next(clonedRequest).pipe(
    catchError((error: any) => {
      if (error instanceof HttpErrorResponse) {
    
        console.log(req.url)
        if (req.url.includes('/user/partial-data') || req.url.includes('/file/profile-img')) {
          // ✅ Don't act on errors from auth endpoints
          console.log("POPUSI MI KURAC")
          return throwError(() => error);
        }


        if (!navigator.onLine) {
          console.error('No Internet Connection');
          snackBar.openSnackBar('You are offline. Please check your internet connection.');
        } else if (error.status === 0) {
          console.error('Cannot reach the server');
          snackBar.openSnackBar('The server is unreachable. Please try again later.');
        } else if (error.status === 401) {
          console.error('Unauthorized');
          zone.run(() => {
            if(jwtService.isLoggedIn()) jwtService.setUser(null);
            if(!req.url.includes('/auth/')){
              console.error('Unauthorized non auth - Redirecting to login');
              router.navigate(['/auth/login'], { replaceUrl: true });
   
              snackBar.openSnackBar('Your session has expired. Please log in again.');
            }
          });
        } else if (error.status === 403) {
          console.error('Forbidden - No access');
         
         
        } else if (error.status >= 500) {
          console.error(`Server Error (${error.status}): ${error.message}`);
          snackBar.openSnackBar('Something went wrong on our end. Please try again later.');
        } 
        else {
          console.error(`HTTP Error (${error.status}): ${error.message}`);
          // snackBar.openSnackBar(error.error?.message || 'An unexpected error occurred.');
        }

        
  
      }

      return throwError(() => error);
    })
  );
};