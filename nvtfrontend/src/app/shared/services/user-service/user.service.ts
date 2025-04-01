import { Injectable } from '@angular/core';
import { PartialUserData } from '../../model';
import { environment } from '../../../../environments/environment.development';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }
  
  getPartialUserData(): Observable<PartialUserData>{
    return this.http.get<PartialUserData>(environment.apiUrl + '/user/partial-data');
  }

  
}
