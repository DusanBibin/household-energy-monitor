import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  constructor(private http: HttpClient) {}

  getProfileImage(): Observable<Blob>{
    return this.http.get(environment.apiUrl + '/file/profile-img', {responseType: 'blob'});
  }

}
