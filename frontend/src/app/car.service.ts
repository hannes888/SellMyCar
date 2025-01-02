import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CarService {
  private apiUrl = '/api/car';

  constructor(private http: HttpClient) { }

  getCarStatistics(car: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, car);
  }
}
