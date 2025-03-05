import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { RealestateDoc, CityDoc, MunicipalityDoc, RegionDoc } from './model/client-model';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment.development';
import { LocationDTO } from '../../../shared/model';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

   constructor(private http: HttpClient) {}
  
    
    searchVacantHousehold(query: String): Observable<(CityDoc | MunicipalityDoc | RegionDoc | RealestateDoc)[]>{
      return this.http.get<(CityDoc | MunicipalityDoc | RegionDoc | RealestateDoc)[]>(environment.apiUrl + '/realestate/search?query=' + query);
    }

    aggregate(topLeft: LocationDTO, bottomRight: LocationDTO, zoomLevel: number, filterType?: string, filterDocId?: string): Observable<RealestateDoc[]>{

      let params = new HttpParams()
      .set('topLeftLon', topLeft.lon)
      .set('topLeftLat', topLeft.lat)
      .set('bottomRightLon', bottomRight.lon)
      .set('bottomRightLat', bottomRight.lat)
      .set('zoomLevel', zoomLevel);

      if (filterType) {
        params = params.set('filterType', filterType);
      }
      if (filterDocId) {
        params = params.set('filterDocId', filterDocId);
      }

      return this.http.get<RealestateDoc[]>(environment.apiUrl + '/realestate/aggregate', { params })
    }
  
}
