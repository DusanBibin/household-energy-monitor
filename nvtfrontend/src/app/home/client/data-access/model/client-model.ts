import { UserSummaryDTO } from "../../../../auth/data-access/model/auth-model";

export interface RealestateDoc{
    id: string,
    dbId: number,
    address: string,
    type: string,
    location:string
}

export interface CityDoc {
  id: string;
  dbId: number;
  city: string;
}

export interface MunicipalityDoc {
  id: string;
  dbId: number;
  municipality: string;
}

export interface RegionDoc {
  id: string;
  region: string;
}

export interface RealestateImagePathsDTO{
  id: number,
  paths: string[]
}

export interface VacantApartmentDTO{
  id: number,
  apartmentNumber: string
}

export interface HouseholdDetailsDTO{

  user: UserSummaryDTO
  addressStreet: string,
  addressNum: string,
  city: string,
  municipality: string,
  region: string,
  realestateType: string,
  lat: number,
  lon: number,
  totalFloors: number,
  images: string[],
  apartmentNum: number,
  size: number

}