import { inject } from '@angular/core';
import { JwtService } from '../services/jwt-service/jwt.service';
import { CanActivateFn, Router } from '@angular/router';
import { SnackBarService } from '../services/snackbar-service/snackbar.service';
import { forkJoin, of } from 'rxjs';
import { UserService } from '../services/user-service/user.service';
import { FileService } from '../services/file-service/file.service';
import { map, catchError } from 'rxjs/operators';
import { AuthService } from '../../auth/data-access/auth.service';
import { error } from 'console';


export const authGuard: CanActivateFn = (route, state) => {
  const isBrowser = (): boolean => typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  const jwtService = inject(JwtService);
  const router = inject(Router);
  const snackBar = inject(SnackBarService);
  const allowedRoles = route.data['roles'] as string[];
  const currentPath = route.url[0].path;
  const allowedRole = allowedRoles[0];
  const userService = inject(UserService);
  const fileService = inject(FileService);
  const authService = inject(AuthService)

  if (!isBrowser()) {
    console.log("Not running in a browser environment.");
    return of(false);
  }
 


  if (jwtService.isLoggedIn()) {
 
    if (jwtService.hasRole(allowedRoles)) {
      if (
        jwtService.hasRole(['SUPERADMIN']) &&
        jwtService.isFirstSuperadminLogin() &&
        state.url !== "/auth/change-password"
      ) {
      
        router.navigate(['/auth/change-password'], { replaceUrl: true });
        return of(false);
      }

      if (currentPath.includes('auth') && !jwtService.isFirstSuperadminLogin()) {
        router.navigate(['/home'], { replaceUrl: true });
        return of(false);
      }
    }
    console.log("prosli smo auth guard")
    return of(true);
  } else {
  
    
    // return authService.checkAuth().pipe(
    //   map((isAuthenticated) => {
    //     if (isAuthenticated) {
    //       console.log("Received user data in auth guard (post-checkAuth)");
    //       console.log("autentifikovani s")
    //       if (
    //         jwtService.hasRole(['SUPERADMIN']) &&
    //         jwtService.isFirstSuperadminLogin() &&
    //         state.url !== "/auth/change-password"
    //       ) {
    //         console.log("navigiramo na change password prvi put je admin")
    //         router.navigate(['/auth/change-password'], { replaceUrl: true });
    //         return false;
    //       }

    //       if (currentPath.includes('auth') && !jwtService.isFirstSuperadminLogin()) {
    //         router.navigate(['/home'], { replaceUrl: true });
    //         return false;
    //       }
    //       console.log("authentifickovnaoi smo")
          
    //       return true
    //     } else {
    //       if (currentPath.includes('home')) {
    //         console.log("includuje home, navigiramo na auth")
    //         router.navigate(['/auth'], { replaceUrl: true });
    //       }else if(currentPath.includes('auth')){
    //         console.log("includuje auth nastavljamo dalje")
    //         return true;
    //       }
    //       console.log("authentifikovani nismo")
    //       return false;
    //     }
    //   }),
    //   catchError((error) => {
    //     console.error("Error retrieving user data in auth guard:", error);
    //     router.navigate(['/auth'], { replaceUrl: true });
    //     return of(false);
    //   })
    // );




    return forkJoin({
      partialUserData: userService.getPartialUserData(),
      profileImg: fileService.getProfileImage()
    }).pipe(
      map(({partialUserData, profileImg }) => {
        let imgUri: string = URL.createObjectURL(profileImg);
        jwtService.setUser({ data: partialUserData, profileImage: imgUri });
     

        if (jwtService.hasRole(allowedRoles)) {
          if (
            jwtService.hasRole(['SUPERADMIN']) &&
            jwtService.isFirstSuperadminLogin() &&
            state.url !== "/auth/change-password"
          ) {
            router.navigate(['/auth/change-password'], { replaceUrl: true });
            return false;
          }

          if (currentPath.includes('auth') && !jwtService.isFirstSuperadminLogin()) {
            router.navigate(['/home'], { replaceUrl: true });
            return false;
          }
        }

        return true;
      }),
      catchError((error) => {
        console.error("Error retrieving user data in auth guard:", error);
 
        if (currentPath.includes('home')) {
         
          router.navigate(['/auth'], { replaceUrl: true });
          return of(false);
        }
        return of(true);
      })
  );

  }
};



// return forkJoin({
//   partialUserData: userService.getPartialUserData(),
//   profileImg: fileService.getProfileImage()
// }).pipe(
//   map(({partialUserData }) => {
//     let imgUri: string = URL.createObjectURL(profileImg);
//     jwtService.setUser({ data: partialUserData, profileImage: "" });
//     console.log("Received user data in auth guard.");

//     if (jwtService.hasRole(allowedRoles)) {
//       if (
//         jwtService.hasRole(['SUPERADMIN']) &&
//         jwtService.isFirstSuperadminLogin() &&
//         state.url !== "/auth/change-password"
//       ) {
//         router.navigate(['/auth/change-password'], { replaceUrl: true });
//         return false;
//       }

//       if (currentPath.includes('auth') && !jwtService.isFirstSuperadminLogin()) {
//         router.navigate(['/home'], { replaceUrl: true });
//         return false;
//       }
//     }

//     return true;
//   }),
//   catchError((error) => {
//     console.error("Error retrieving user data in auth guard:", error);
//     if (currentPath.includes('home')) {
//       router.navigate(['/auth'], { replaceUrl: true });
//     }
//     return of(false);
//   })
// );