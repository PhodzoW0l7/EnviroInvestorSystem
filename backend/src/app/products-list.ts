import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Product } from './portfolio';

@Component({
  selector: 'app-products-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatChipsModule, MatTooltipModule],
  template: `
    <div class="products-wrapper">
      <div class="product-card" *ngFor="let product of products; let i = index"
           [style.animation-delay]="i * 80 + 'ms'">

        <!-- Product type badge -->
        <div class="product-header">
          <div class="type-badge" [ngClass]="product.type === 'RETIREMENT' ? 'badge-retirement' : 'badge-savings'">
            <mat-icon>{{ product.type === 'RETIREMENT' ? 'account_balance' : 'savings' }}</mat-icon>
            {{ product.type }}
          </div>
          <div class="product-actions">
            <button class="action-btn withdraw-btn"
                    [matTooltip]="product.type === 'RETIREMENT' ? 'Age > 65 required' : 'Create withdrawal notice'"
                    (click)="onWithdraw.emit(product)">
              <mat-icon>arrow_circle_up</mat-icon>
              Withdraw
            </button>
            <button class="action-btn csv-btn"
                    matTooltip="Download CSV statement"
                    (click)="onExport.emit(product.id)">
              <mat-icon>file_download</mat-icon>
              CSV
            </button>
          </div>
        </div>

        <!-- Product details -->
        <div class="product-body">
          <div class="product-name">{{ product.name }}</div>
          <div class="product-balance">
            <span class="balance-label">Current Balance</span>
            <span class="balance-amount">{{ product.currentBalance | currency:'ZAR':'R':'1.2-2' }}</span>
          </div>
        </div>

        <!-- Balance bar (visual indicator: % of max balance across products) -->
        <div class="balance-bar-track">
          <div class="balance-bar-fill"
               [style.width]="getBalancePercent(product) + '%'"
               [ngClass]="product.type === 'RETIREMENT' ? 'bar-retirement' : 'bar-savings'">
          </div>
        </div>

      </div>

      <div class="empty-state" *ngIf="products.length === 0">
        <mat-icon>inventory_2</mat-icon>
        <p>No products found for this investor.</p>
      </div>
    </div>
  `,
  styleUrls: ['./app.css']
})
export class ProductsListComponent {
  @Input() products: Product[] = [];
  @Output() onWithdraw = new EventEmitter<Product>();
  @Output() onExport = new EventEmitter<number>();

  getBalancePercent(product: Product): number {
    if (!this.products.length) return 0;
    const max = Math.max(...this.products.map(p => p.currentBalance));
    return max === 0 ? 0 : Math.round((product.currentBalance / max) * 100);
  }
}