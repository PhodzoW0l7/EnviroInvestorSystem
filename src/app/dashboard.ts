import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { PortfolioService } from './portfolio.service';
import { ProductsListComponent } from './products-list';
import { InvestorPortfolio, InvestorSummary, Product } from './portfolio';
import { WithdrawalDialogComponent } from './withdrawal-form';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatIconModule, MatProgressSpinnerModule,
    MatDialogModule, MatSnackBarModule, MatTooltipModule, ProductsListComponent
  ],
  template: `

    <!-- Top Navbar -->
    <nav class="navbar">
      <div class="navbar-brand">
        <mat-icon class="brand-icon">trending_up</mat-icon>
        Enviro350 <span class="brand-thin">Investments</span>
      </div>
      <div class="navbar-right" *ngIf="portfolio">
        <div class="nav-user">
          <div class="user-avatar">{{ getInitials() }}</div>
          <div class="user-info">
            <span class="user-name">{{ portfolio.fullName }}</span>
            <span class="user-email">{{ portfolio.email }}</span>
          </div>
        </div>
      </div>
    </nav>

    <!-- Page Wrapper -->
    <div class="page-wrapper">

      <!-- Sidebar -->
      <aside class="sidebar">
        <div class="sidebar-heading">Investors</div>
        <nav class="sidebar-nav">
          <a class="nav-link"
             *ngFor="let inv of investors"
             [class.active]="selectedInvestorId === inv.id"
             (click)="switchInvestor(inv.id)">
            <mat-icon>person</mat-icon>
            {{ inv.label }}
          </a>
        </nav>

        <div class="sidebar-heading mt-3">Navigation</div>
        <nav class="sidebar-nav">
          <a class="nav-link nav-link-active-section">
            <mat-icon>dashboard</mat-icon>
            Portfolio
          </a>
        </nav>

        <div class="sidebar-spacer"></div>
        <div class="sidebar-footer">
          <mat-icon>info_outline</mat-icon>
          Enviro350 v1.0
        </div>
      </aside>

      <!-- Main Content -->
      <main class="main-content">

        <!-- Page heading -->
        <div class="page-header">
          <div>
            <h1 class="page-title">Portfolio Dashboard</h1>
            <p class="page-subtitle">
              {{ portfolio ? portfolio.fullName + ' · Age ' + portfolio.age : 'Loading investor data...' }}
            </p>
          </div>
          <div class="page-actions" *ngIf="portfolio">
            <span class="live-badge">
              <span class="live-dot"></span> Live
            </span>
          </div>
        </div>

        <!-- Loading -->
        <div class="loading-state" *ngIf="loading">
          <div class="spinner"></div>
          <p>Fetching portfolio data...</p>
        </div>

        <!-- Error -->
        <div class="alert alert-danger" *ngIf="errorMessage && !loading">
          <mat-icon>error_outline</mat-icon>
          {{ errorMessage }}
        </div>

        <ng-container *ngIf="portfolio && !loading">

          <!-- KPI Cards -->
          <div class="kpi-grid">
            <div class="card border-left-primary">
              <div class="card-body">
                <div class="text-xs text-primary mb-1">Total Portfolio Value</div>
                <div class="kpi-value">{{ getTotalBalance() | currency:'ZAR':'R':'1.2-2' }}</div>
                <mat-icon class="kpi-icon text-gray-300">account_balance_wallet</mat-icon>
              </div>
            </div>
            <div class="card border-left-success">
              <div class="card-body">
                <div class="text-xs text-success mb-1">Total Products</div>
                <div class="kpi-value">{{ portfolio.products.length }}</div>
                <mat-icon class="kpi-icon text-gray-300">folder_open</mat-icon>
              </div>
            </div>
            <div class="card border-left-info">
              <div class="card-body">
                <div class="text-xs text-info mb-1">Savings Balance</div>
                <div class="kpi-value">{{ getSavingsBalance() | currency:'ZAR':'R':'1.2-2' }}</div>
                <mat-icon class="kpi-icon text-gray-300">savings</mat-icon>
              </div>
            </div>
            <div class="card border-left-warning">
              <div class="card-body">
                <div class="text-xs text-warning mb-1">Retirement Balance</div>
                <div class="kpi-value">{{ getRetirementBalance() | currency:'ZAR':'R':'1.2-2' }}</div>
                <mat-icon class="kpi-icon text-gray-300">account_balance</mat-icon>
              </div>
            </div>
          </div>

          <!-- Products table -->
          <app-products-list
            [products]="portfolio.products"
            (onWithdraw)="openWithdrawalDialog($event)"
            (onExport)="downloadCsv($event)">
          </app-products-list>

        </ng-container>
      </main>
    </div>
  `,
 styleUrls: ['./app.css']
})
export class DashboardComponent implements OnInit {
  private portfolioService = inject(PortfolioService);
  private dialog           = inject(MatDialog);
  private snackBar         = inject(MatSnackBar);
  private cdr              = inject(ChangeDetectorRef);

  portfolio?: InvestorPortfolio;
  loading = false;
  errorMessage?: string;
  selectedInvestorId = 1;

  investors: InvestorSummary[] = [
    { id: 1, label: 'Sipho Dlamini' },
    { id: 2, label: 'John Smith' },
    { id: 3, label: 'Priya Naidoo' }
  ];

  ngOnInit(): void { this.loadPortfolio(this.selectedInvestorId); }

  switchInvestor(id: number): void {
    if (this.selectedInvestorId === id) return;
    this.selectedInvestorId = id;
    this.portfolio = undefined;
    this.loadPortfolio(id);
  }

  private loadPortfolio(id: number): void {
    this.loading = true;
    this.errorMessage = undefined;
    this.portfolioService.getPortfolio(id).subscribe({
      next: (data) => { this.portfolio = data; this.loading = false; this.cdr.detectChanges(); },
      error: (err)  => {
        this.errorMessage = err?.error?.error || err?.message || 'Failed to load portfolio.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  openWithdrawalDialog(product: Product): void {
    const dialogRef = this.dialog.open(WithdrawalDialogComponent, {
      width: '420px',
      panelClass: 'light-dialog',
      data: { product }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.portfolioService.createWithdrawal(result).subscribe({
          next: () => {
            this.snackBar.open('✓ Withdrawal notice submitted successfully', 'Close', {
              duration: 4000, panelClass: 'snack-success'
            });
            this.loadPortfolio(this.selectedInvestorId);
          },
          error: (err) => {
            const msg = err?.error?.error || 'Error processing withdrawal.';
            this.snackBar.open('⚠ ' + msg, 'Close', { duration: 6000, panelClass: 'snack-error' });
          }
        });
      }
    });
  }

  downloadCsv(productId: number): void {
    this.portfolioService.exportCsv(productId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `withdrawal_statement_product_${productId}.csv`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => this.snackBar.open('CSV export failed.', 'Close', { duration: 3000 })
    });
  }

  getInitials(): string {
    if (!this.portfolio) return '';
    return (this.portfolio.name[0] + this.portfolio.surname[0]).toUpperCase();
  }

  getTotalBalance():      number { return this.portfolio?.products.reduce((s,p) => s + p.currentBalance, 0) ?? 0; }
  getSavingsBalance():    number { return this.portfolio?.products.filter(p => p.type === 'SAVINGS').reduce((s,p) => s + p.currentBalance, 0) ?? 0; }
  getRetirementBalance(): number { return this.portfolio?.products.filter(p => p.type === 'RETIREMENT').reduce((s,p) => s + p.currentBalance, 0) ?? 0; }
}