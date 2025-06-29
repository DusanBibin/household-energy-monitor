import { inject } from '@angular/core';
import { JwtService } from '../services/jwt-service/jwt.service';
import { CanActivateFn, Router } from '@angular/router';
import { SnackBarService } from '../services/snackbar-service/snackbar.service';
import { UserService } from '../services/user-service/user.service';
import { FileService } from '../services/file-service/file.service';
import { forkJoin, of, catchError, map } from 'rxjs';

export const roleGuard: CanActivateFn = (route, state) => {
  const isBrowser = (): boolean => typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  const jwtService = inject(JwtService);
  const router = inject(Router);
  const snackBar = inject(SnackBarService);
  const allowedRoles = route.data['roles'] as string[];
  const userService = inject(UserService)
  const fileService = inject(FileService)
  
  const allowedRole = allowedRoles[0]
  const currentPath = route.url[0].path;

  if (!isBrowser()) {
    console.log("Not running in a browser environment.");
    return false; 
  };



  return forkJoin({
    partialUserData: userService.getPartialUserData(),
    profileImg: fileService.getProfileImage(),
  }).pipe(
    map(({ partialUserData, profileImg }) => {
      const imgUri = URL.createObjectURL(profileImg);
      jwtService.setUser({ data: partialUserData, profileImage: imgUri });

      console.log("DOBILI SMO SLIKE I PARTIAL DATA")

      const hasAccess = currentPath.toLowerCase().includes(allowedRole.toLowerCase()) &&
                        jwtService.hasRole([allowedRole]);

      if (!hasAccess) {
        router.navigate(['/home'], { replaceUrl: true });
        return false;
      }

      return true;
    }),
    catchError(err => {
      router.navigate(['/home'], { replaceUrl: true });

      console.log("NAISLI SMO NA GRESKE")
      return of(false);
    })
  );

};
