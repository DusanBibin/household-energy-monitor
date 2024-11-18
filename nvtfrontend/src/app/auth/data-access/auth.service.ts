import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { AuthRequestDTO, AuthResponseDTO } from './model/auth-model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) {}


  login(request: AuthRequestDTO): Observable<AuthResponseDTO>{
    return this.http.post<AuthResponseDTO>(environment.apiUrl + '/auth/authenticate',  request);
  }

}


