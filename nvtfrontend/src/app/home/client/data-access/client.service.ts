import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RealestateDoc } from './model/client-model';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

   constructor(private http: HttpClient) {}
  
    
    searchVacantHousehold(query: String): Observable<RealestateDoc[]>{
      return this.http.get<RealestateDoc[]>(environment.apiUrl + '/realestate/search?query=' + query);
    }
  
}
