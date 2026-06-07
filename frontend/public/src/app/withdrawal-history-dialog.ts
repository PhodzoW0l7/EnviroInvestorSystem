import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-withdrawal-history-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule],
  template: `
    <h2 mat-dialog-title>
      <mat-icon style="vertical-align: middle; margin-right: 8px;">history</mat-icon>
      Withdrawal History — {{ data.productName }}
    </h2>

    <mat-dialog-content>

      <!-- Empty State -->
      <div class="empty-state" *ngIf="data.history.length === 0">
        <mat-icon>inbox</mat-icon>
        <p>No withdrawal history found for this product.</p>
      </div>

      <!-- History Table -->
      <div class="table-card" *ngIf="data.history.length > 0">
        <table class="data-table">
          <thead>
            <tr>
              <th>Notice ID</th>
              <th>Product</th>
              <th>Amount</th>
              <th>Date</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let notice of data.history">
              <td>#{{ notice.id }}</td>
              <td>{{ notice.productName }}</td>
              <td>{{ notice.withdrawalAmount | currency:'ZAR':'R':'1.2-2' }}</td>
              <td>{{ notice.requestDate | date:'dd MMM yyyy, HH:mm' }}</td>
              <td>
                <span class="status-badge" [class.badge-success]="notice.status === 'SUCCESS'">
                  {{ notice.status }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="dialogRef.close()">Close</button>
    </mat-dialog-actions>
  `,
  styles: [`
    h2 { display: flex; align-items: center; }
    mat-dialog-content { min-width: 600px; max-height: 400px; overflow-y: auto; }
    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 40px;
      color: #94a3b8;
      gap: 8px;
    }
    .empty-state mat-icon { font-size: 48px; width: 48px; height: 48px; }
    .table-card { overflow: hidden; border-radius: 8px; border: 1px solid #e2e8f0; }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th {
      background: #f8fafc;
      padding: 10px 14px;
      text-align: left;
      font-size: 12px;
      font-weight: 600;
      color: #64748b;
      text-transform: uppercase;
      border-bottom: 1px solid #e2e8f0;
    }
    .data-table td {
      padding: 12px 14px;
      font-size: 14px;
      color: #334155;
      border-bottom: 1px solid #f1f5f9;
    }
    .data-table tbody tr:last-child td { border-bottom: none; }
    .data-table tbody tr:hover { background: #f8fafc; }
    .status-badge {
      padding: 4px 10px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 600;
      background: #f1f5f9;
      color: #64748b;
    }
    .badge-success { background: #dcfce7; color: #16a34a; }
  `]
})
export class WithdrawalHistoryDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<WithdrawalHistoryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { productName: string; history: any[] }
  ) {}
}