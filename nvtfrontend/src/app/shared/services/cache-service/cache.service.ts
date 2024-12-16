import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CacheService {

  private cache = new Map<string, any[]>();

  public cache$ = new BehaviorSubject<any[] | null>(null); 

  set(key: string, data: any[]): void {
    if (this.cache.has(key)) {
      throw new Error(`Data already exists for key '${key}'. Use a different key or delete the existing one first.`);
    }
    this.cache.set(key, data);
    this.cache$.next(data); 
  }


  
  get(key: string): any[] | null {
    const data = this.cache.get(key) ?? null; 
    this.cache$.next(data); 
    return data;
  }

  
  clear(key: string): void {
    this.cache.delete(key);
    this.cache$.next(null); 
  }
}




