import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {DashboardComponent} from './dashboard';

@Component({
  selector: 'app-root',
   standalone: true,
  imports: [DashboardComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  title = 'InvestorSystemFrontend';
}