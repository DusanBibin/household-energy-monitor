import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RealestateDoc, CityDoc, MunicipalityDoc, RegionDoc } from './model/client-model';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

   constructor(private http: HttpClient) {}
  
    
    searchVacantHousehold(query: String): Observable<(CityDoc | MunicipalityDoc | RegionDoc | RealestateDoc)[]>{
      return this.http.get<(CityDoc | MunicipalityDoc | RegionDoc | RealestateDoc)[]>(environment.apiUrl + '/realestate/search?query=' + query);
    }
  
}
