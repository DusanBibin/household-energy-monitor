import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { JwtPayload } from '../model';

@Injectable({
  providedIn: 'root'
})
export class JwtService {

  constructor(private http: HttpClient) { }


  private isBrowser(): boolean {
    return typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  }

  public login(jwt: string): void{
    this.logout();
    if(this.isBrowser()){
      localStorage.setItem('access_token', jwt);
    }
  }


  public logout(): void{
    if(this.isBrowser()){
      localStorage.removeItem('access_token');
    }
  }

  getToken(): string | null {
    return this.isBrowser() ? localStorage.getItem('access_token') : null;
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    return !!token && !this.isTokenExpired(token);
  }

  hasRole(requiredRole: string): boolean {
    const token = this.getToken();
    if (!token) return false;

    const decoded = this.decodeToken(token);
    const roles = decoded?.role || [];
    return roles.some((role: { authority: string }) => role.authority === requiredRole);
  }

  isFirstSuperadminLogin(): boolean {
    
    const token = this.getToken();
    if(!token) return false;

    if(!this.hasRole("SUPERADMIN")) return false;
    const payload = this.decodeToken(token);
    
    return payload.isFirstLogin;

  }


  isTokenExpired(token: string): boolean {
    const payload = this.decodeToken(token);
    if (!payload || !payload.exp) {
      return true;
    }
    const now = Math.floor(new Date().getTime() / 1000);
    return payload.exp < now;
  }

  decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload));
    } catch (error) {
      return null;
    }
  }
}
