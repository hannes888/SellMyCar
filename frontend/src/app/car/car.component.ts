import {Component} from '@angular/core';
import {CarService} from '../car.service';
import {NgForOf, NgIf} from '@angular/common';
import {MatButton} from '@angular/material/button';
import {MatFormField} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {FormsModule} from '@angular/forms';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatCard} from '@angular/material/card';
import {MatProgressSpinner} from '@angular/material/progress-spinner';

@Component({
  selector: 'app-car',
  imports: [
    NgIf,
    MatButton,
    MatFormField,
    MatInput,
    FormsModule,
    MatSelect,
    MatOption,
    NgForOf,
    MatCard,
    MatProgressSpinner
  ],
  templateUrl: './car.component.html',
  styleUrl: './car.component.css',
  standalone: true
})
export class CarComponent {
  car = {make: '', model: '', year: null, mileage: null};
  statistics: any;
  carMakes: string[] = [];
  isLoading: boolean = false;

  constructor(private carService: CarService) {
  }

  ngOnInit() {
    this.carService.getCarMakes().subscribe((makes: string[]) => {
      this.carMakes = makes;
    });
  }

  getStatistics() {
    this.isLoading = true;
    this.car.make.toUpperCase();
    this.carService.getCarStatistics(this.car).subscribe({
      next: (data: any) => {
        this.isLoading = false;
        this.statistics = data;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }
}
