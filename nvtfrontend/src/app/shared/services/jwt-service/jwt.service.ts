import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthService } from '../../../auth/data-access/auth.service';
import { forkJoin } from 'rxjs';
import { FileService } from '../file-service/file.service';
import { UserService } from '../user-service/user.service';
import { PartialUserData } from '../../model';

@Injectable({
  providedIn: 'root'
})
export class JwtService {
  private userSubject = new BehaviorSubject<{ data: PartialUserData, profileImage: string } | null>(null);
  user$ = this.userSubject.asObservable(); // Observable for components to subscribe to

  constructor(private authService: AuthService) {
    console.log("da li se ovo upalilo jwt service jajajahh")
  }



  setUser(user: { data: PartialUserData, profileImage: string } | null): void {
    this.userSubject.next(user);
  }

  // Get the current user details (if already fetched)
  getUser(): { data: PartialUserData, profileImage: string } | null {
    return this.userSubject.value;
  }

  // Check if user is logged in
  isLoggedIn(): boolean {
    return this.userSubject.value !== null;
  }

  // Check if user has a specific role
  hasRole(allowedRoles: string[]): boolean {
    const user = this.getUser();
    return user ? allowedRoles.includes(user.data.role) : false;
  }

  // Check if superadmin needs to change password
  isFirstSuperadminLogin(): boolean {
    const user = this.getUser();
    return user?.data.role === 'SUPERADMIN' && user.data.firstLogin === true;
  }

  // Logout by making a request to the backend (JWT cookie will be removed)
  // logout(): void {
  //   this.authService.logout().subscribe({
  //     next: value => {
  //       this.userSubject.next(null);
  //     },
  //     error: err => {
  //       console.log(err)
  //     }
  //   })
  // }
}
