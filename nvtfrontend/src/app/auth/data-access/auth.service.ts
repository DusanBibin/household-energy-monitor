import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { AuthRequestDTO, AuthResponseDTO, SuperadminPasswordChangeDTO } from './model/auth-model';
import { ResponseMessage } from '../../shared/model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) {}


  login(request: AuthRequestDTO): Observable<AuthResponseDTO>{
    return this.http.post<AuthResponseDTO>(environment.apiUrl + '/auth/authenticate',  request);
  }


  changeSuperadminPassword(request: SuperadminPasswordChangeDTO): Observable<ResponseMessage>{
    return this.http.put<ResponseMessage>(environment.apiUrl + '/auth/change-superadmin-password', request)
  }

}


