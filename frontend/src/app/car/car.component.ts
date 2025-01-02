import { Component } from '@angular/core';
import {CarService} from '../car.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-car',
  imports: [
    NgIf
  ],
  templateUrl: './car.component.html',
  styleUrl: './car.component.css',
  standalone: true
})
export class CarComponent {
  car = { make: 'AUDI', model: 's3', year: 2010 };
  statistics: any;

  constructor(private carService: CarService) { }

  getStatistics() {
    this.carService.getCarStatistics(this.car).subscribe((data: any) => {
      this.statistics = data;
    });
  }
}
