import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { AuthRequestDTO, AuthResponseDTO, SuperadminPasswordChangeDTO } from './model/auth-model';
import { PartialUserData, ResponseMessage } from '../../shared/model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) {}


  login(request: AuthRequestDTO): Observable<PartialUserData>{
    return this.http.post<PartialUserData>(environment.apiUrl + '/auth/authenticate',  request);
  }


  changeSuperadminPassword(request: SuperadminPasswordChangeDTO): Observable<ResponseMessage>{
    return this.http.put<ResponseMessage>(environment.apiUrl + '/auth/change-superadmin-password', request, { withCredentials: true });
  }

  registerClient(request: FormData): Observable<ResponseMessage>{
    return this.http.post<ResponseMessage>(environment.apiUrl + '/auth/register', request); 
  }


  logout(){
    return this.http.post(environment.apiUrl + '/auth/logout', {}, { withCredentials: true })
  }

  checkAuth(): Observable<boolean>{
    return this.http.get<boolean>(environment.apiUrl + '/auth/check')
  }

}


