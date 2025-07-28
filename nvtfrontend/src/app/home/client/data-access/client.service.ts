import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { RealestateDoc, CityDoc, MunicipalityDoc, RegionDoc, RealestateImagePathsDTO, VacantApartmentDTO, HouseholdDetailsDTO, HouseholdRequestDTO } from './model/client-model';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment.development';
import { LocationDTO } from '../../../shared/model';
import { PagedResponse } from '../ui/household-requests-list/household-requests-list.component';
import { HouseholdRequestPreviewDTO } from './model/client-model';
import { request } from 'http';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

   constructor(private http: HttpClient) {}
  
    
    searchVacantHousehold(query: String): Observable<(CityDoc | MunicipalityDoc | RegionDoc | RealestateDoc)[]>{
      return this.http.get<(CityDoc | MunicipalityDoc | RegionDoc | RealestateDoc)[]>(environment.apiUrl + '/realestate/search?query=' + query, {withCredentials: true});
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

      return this.http.get<RealestateDoc[]>(environment.apiUrl + '/realestate/aggregate', { params, withCredentials: true} )
    }


    getRealestateImagePaths(realestateIds: number[]): Observable<RealestateImagePathsDTO[]>{
      return this.http.post<RealestateImagePathsDTO[]>(environment.apiUrl + '/realestate/paged-realestate-image-paths', realestateIds, {withCredentials: true})
    }

    getVacantRealestateHouseHolds(realestateId: number): Observable<VacantApartmentDTO[]>{
      return this.http.get<VacantApartmentDTO[]>(environment.apiUrl + '/realestate/' + realestateId + '/vacant-apartment-numbers', {withCredentials: true})
    }

    getHouseholdDetails(realestateId: number, householdId: number): Observable<HouseholdDetailsDTO>{
      return this.http.get<HouseholdDetailsDTO>(environment.apiUrl + '/realestate/' + realestateId + '/household/' + householdId , {withCredentials: true})
    }


    createHouseholdClaim(realestateId: number, householdId: number, files: File[]): Observable<HouseholdDetailsDTO>{

      const formData = new FormData();
      files.forEach(file => formData.append('files', file))


      return this.http.post<HouseholdDetailsDTO>(environment.apiUrl + '/realestate/' + realestateId + '/household/' + householdId + '/household-request', formData, {withCredentials: true})
    }


    getClientRequests(status: string | null, page: number = 0, size: number = 10, sortField: string = 'requestSubmitted', sortDir: string = 'desc'
    ): Observable<PagedResponse<HouseholdRequestPreviewDTO>> {

      let params = new HttpParams()
        .set('page', page)
        .set('size', size)
        .set('sortField', sortField)
        .set('sortDir', sortDir);
  
      if (status) {
        params = params.set('status', status);
      }
  
      return this.http.get<PagedResponse<HouseholdRequestPreviewDTO>>(environment.apiUrl + "/household-request", { params, withCredentials: true }, );
    }


    getHouseholdRequestDetails(realestateId: number, householdId: number, requestId: number): Observable<HouseholdRequestDTO>{
      return this.http.get<HouseholdRequestDTO>(environment.apiUrl + '/realestate/' + realestateId + '/household/' + householdId + '/household-request/' + requestId)
    }


    processHouseholdRequest(realestateId: number, householdId: number, requestId: number, isAccepted: boolean, denyReason: string | null): Observable<HouseholdRequestDTO>{

      let decision = "accept"
      if(!isAccepted) decision = "decline"
      return this.http.put<HouseholdRequestDTO>(environment.apiUrl + '/realestate/' + realestateId + '/household/' + householdId + '/household-request/' + requestId + "/" + decision, denyReason)
  
    }
    


  
}
