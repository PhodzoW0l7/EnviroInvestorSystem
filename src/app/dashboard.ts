import {Component,OnInit,inject,ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PortfolioService } from './portfolio.service';
import { ProductsListComponent } from './products-list';
import { InvestorPortfolio, Product } from './portfolio';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { WithdrawalDialogComponent } from './withdrawal-form';

@Component({
    selector:'app-dashboard',
    standalone:true,
    imports: [
        CommonModule,MatToolbarModule,MatCardModule,MatProgressSpinnerModule,
        ProductsListComponent,MatDialogModule,MatSnackBarModule
    ],
    template: `
    <mat-toolbar color="primary">
      <span>Investor Portfolio Dashboard</span>
    </mat-toolbar>

    <div class="dashboard-container" *ngIf="portfolio; else loading">
      <!-- Investor Info Card -->
      <mat-card class="investor-card">
        <mat-card-header>
          <mat-card-title>{{portfolio.fullName}}</mat-card-title>
          <mat-card-subtitle>Age: {{portfolio.age}}</mat-card-subtitle>
        </mat-card-header>
      </mat-card>

      <!-- Products Standalone Component -->
      <h3>Your Investment Products</h3>
      <app-products-list 
        [products]="portfolio.products"
        (onWithdraw)="openWithdrawalDialog($event)"
        (onExport)="downloadCsv($event)">
      </app-products-list>
    </div>

    <ng-template #loading>
      <div class="spinner-container">
        <mat-spinner></mat-spinner>
      </div>
    </ng-template>
  `,
  styles: [`
    .dashboard-container { padding: 24px; max-width: 1200px; margin: 0 auto; }
    .investor-card { margin-bottom: 24px; padding: 16px; }
    .spinner-container { display: flex; justify-content: center; margin-top: 50px; }
    h3 { margin-top: 24px; font-family: Roboto, sans-serif; }
  `]
})

export class DashboardComponent implements OnInit{
    private portfolioService = inject(PortfolioService);
    private dialog = inject(MatDialog);
    private snackBar = inject(MatSnackBar);
    private cdr = inject(ChangeDetectorRef);
    portfolio?:InvestorPortfolio;
    errorMessage?: string;
    ngOnInit(): void {
  this.portfolioService.getPortfolio(1).subscribe({
    next: (data) => {
      this.portfolio = data as InvestorPortfolio;
      this.cdr.detectChanges(); // ← force Angular to re-render
    },
    error: (err) => console.error('Failed to load portfolio', err)
  });
  }

  openWithdrawalDialog(product:Product): void {
    const dialogRef = this.dialog.open(WithdrawalDialogComponent, {
      width: '400px',
      data: { product }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.portfolioService.createWithdrawal(result).subscribe({
          next: (response) => {
            this.snackBar.open('Withdrawal request submitted successfully!', 'Close', { duration: 4000 });
            this.refreshPortfolio(); // Refresh backend numbers after withdrawal
          },
          error: (err) => {
            this.snackBar.open('Error processing request. Check backend validation.', 'Close', { duration: 4000 });
            console.error('Withdrawal failed', err);
          }
        });
      }
    });
    
  }
  private refreshPortfolio() {
    if (this.portfolio?.investorId) {
      this.portfolioService.getPortfolio(this.portfolio.investorId).subscribe(data => this.portfolio = data);
    }
  }

  downloadCsv(productId:number){
    this.portfolioService.exportCsv(productId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `withdrawal_statement_product_${productId}.csv`;
        a.click();
      },
      error: (err) => console.error('CSV export failed', err)
    });
  }
}