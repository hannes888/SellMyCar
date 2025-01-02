import {Component} from '@angular/core';
import {CarComponent} from './car/car.component';


@Component({
  selector: 'app-root',
  imports: [CarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  standalone: true,
})
export class AppComponent {
  title = 'frontend';
}
