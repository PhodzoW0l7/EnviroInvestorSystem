import {Component,Input,Output,EventEmitter} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatTableModule} from '@angular/material/table';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {Product} from './portfolio';

@Component({
    selector:'app-products-list',
    imports:[CommonModule,MatTableModule,MatButtonModule,MatIconModule],
    template:`
    <table mat-table [dataSource]="products" class="mat-elevation-z2">
      <!-- Name Column -->
      <ng-container matColumnDef="name">
        <th mat-header-cell *matHeaderCellDef> Product Name </th>
        <td mat-cell *matCellDef="let product"> {{product.name}} </td>
      </ng-container>

      <!-- Type Column -->
      <ng-container matColumnDef="type">
        <th mat-header-cell *matHeaderCellDef> Type </th>
        <td mat-cell *matCellDef="let product"> {{product.type}} </td>
      </ng-container>

      <!-- Balance Column -->
      <ng-container matColumnDef="balance">
        <th mat-header-cell *matHeaderCellDef> Current Balance </th>
        <td mat-cell *matCellDef="let product"> {{product.currentBalance | currency:'ZAR':'R'}} </td>
      </ng-container>

      <!-- Actions Column -->
      <ng-container matColumnDef="actions">
        <th mat-header-cell *matHeaderCellDef> Actions </th>
        <td mat-cell *matCellDef="let product">
          <button mat-raised-button color="primary" class="action-btn" (click)="onWithdraw.emit(product)">
            Withdraw
          </button>
          <button mat-stroked-button (click)="onExport.emit(product.id)">
            <mat-icon>download</mat-icon> CSV
          </button>
        </td>
      </ng-container>

      <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
      <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
    </table>
  `,
  styles:[`table { width: 100%; margin-top: 16px; }
    .action-btn { margin-right: 8px; }
    `]
})
export class ProductsListComponent{
    @Input() products:Product[]=[];
    @Output() onWithdraw = new EventEmitter<Product>();
    @Output() onExport = new EventEmitter<number>();

    displayedColumns: string[] = ['name', 'type', 'balance', 'actions'];
}