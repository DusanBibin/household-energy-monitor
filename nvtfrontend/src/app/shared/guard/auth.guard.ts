import { inject } from '@angular/core';
import { JwtService } from '../services/jwt-service/jwt.service';
import { CanActivateFn, Router } from '@angular/router';
import { SnackBarService } from '../services/snackbar-service/snackbar.service';

export const authGuard: CanActivateFn = (route, state) => {
  const isBrowser = (): boolean => typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  const jwtService = inject(JwtService);
  const router = inject(Router);
  const snackBar = inject(SnackBarService);
  const allowedRoles = route.data['roles'] as string[];
  const currentPath = route.url[0].path;
  const allowedRole = allowedRoles[0]


  if (!isBrowser()) {
    console.log("Not running in a browser environment.");
    return false; 
  }

  console.log("----------------------------------------")
  console.log(state.url)
  // console.log(jwtService.isLoggedIn())
  // console.log(jwtService.hasRole(allowedRoles))
  console.log(currentPath);
  if(jwtService.isLoggedIn()){
    console.log("ulogovan je")
    if(jwtService.hasRole(allowedRoles)){
      console.log("ima rolu")
      console.log(jwtService.hasRole(['SUPERADMIN']))
      console.log(jwtService.isFirstSuperadminLogin())
      console.log(state.url === "/auth/change-password")

      if(jwtService.hasRole(['SUPERADMIN']) && jwtService.isFirstSuperadminLogin() && !(state.url === "/auth/change-password")){
        console.log("prvi put superadmin")
        router.navigate(['/auth/change-password'], {replaceUrl: true})
        return false;
      }

      if(currentPath.includes('auth') && !jwtService.isFirstSuperadminLogin()){
        console.log("sadrzi home ")
        router.navigate(['/home'], {replaceUrl: true});
        return false;
      }

    }
  }else{
    console.log("nije ulogovan")
    if(currentPath.includes('home')){
      console.log("sadrzi home")
      router.navigate(['/auth'], {replaceUrl: true});
      return false;
    }
  }
  
  
  
  console.log("sve je normalno ")
  
  
  
  // if (jwtService.isLoggedIn()) {
  //   if(jwtService.hasRole(allowedRoles)){
  //     if(jwtService.hasRole(['SUPERADMIN']) && jwtService.isFirstSuperadminLogin()){
  //       router.navigate(['/auth/change-password'])
  //       return false;
  //     }
  
  //     console.log("upravu sii")
  //     return true;
  //   }
  // }else{

  // }

  


  // console.log("nisi upravu")
  // snackBar.openSnackBar("Session expired, please login again");
  // jwtService.logout();
  // router.navigate(['/auth/login'], {replaceUrl: true});



  return true;
};