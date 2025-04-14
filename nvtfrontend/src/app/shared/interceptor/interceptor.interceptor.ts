
import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { JwtService } from '../services/jwt-service/jwt.service';
import { Router } from '@angular/router';
import { SnackBarService } from '../services/snackbar-service/snackbar.service';
import { Observable, throwError, catchError } from 'rxjs';


export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
  const snackBar = inject(SnackBarService);
  const router = inject(Router);

  // Clone the request to ensure cookies are sent
  const clonedRequest = req.clone({ withCredentials: true });

  return next(clonedRequest).pipe(
    catchError((error: any) => {
      if (error instanceof HttpErrorResponse) {
        if (!navigator.onLine) {
          console.error('No Internet Connection');
          snackBar.openSnackBar('You are offline. Please check your internet connection.');
        } else if (error.status === 0) {
          console.error('Cannot reach the server');
          snackBar.openSnackBar('The server is unreachable. Please try again later.');
        } else if (error.status === 401) {
          console.error('Unauthorized - Redirecting to login');
          router.navigate(['/auth/login']);
          snackBar.openSnackBar('Your session has expired. Please log in again.');
        } else if (error.status === 403) {
          console.error('Forbidden - No access');
          // snackBar.openSnackBar('You do not have permission to perform this action.');
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