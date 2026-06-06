import { Component, Inject, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Product } from './portfolio';

@Component({
  selector: 'app-withdrawal-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule
  ],
  template: `
    <h2 mat-dialog-title>Withdrawal Notice</h2>
    <mat-dialog-content>
      <p>Product: <strong>{{ data.product.name }}</strong> ({{ data.product.type }})</p>
      <p>Available Balance: <strong>{{ data.product.currentBalance | currency:'ZAR':'R' }}</strong></p>
      <p style="color: #666; font-size: 13px;">Maximum Allowed (90%): <strong style="color: #b45309;">{{ getMaxAllowed() | currency:'ZAR':'R' }}</strong></p>

      <!-- Alert Box: When the background service is actively running -->
      <div *ngIf="isSubmitting" class="alert-info">
        <mat-spinner diameter="20"></mat-spinner>
        <span>Your request is being processed. Please wait...</span>
      </div>

      <form [formGroup]="withdrawalForm" class="form-container">
        <mat-form-field appearance="outline">
          <mat-label>Withdrawal Amount</mat-label>
          <input matInput type="number" formControlName="withdrawalAmount" placeholder="Enter amount" [readonly]="isSubmitting">
          
          <!-- Standard Validation Errors -->
          <mat-error *ngIf="withdrawalForm.get('withdrawalAmount')?.hasError('required')">
            Amount is required.
          </mat-error>
          <mat-error *ngIf="withdrawalForm.get('withdrawalAmount')?.hasError('min')">
            Amount must be greater than zero.
          </mat-error>
          <mat-error *ngIf="withdrawalForm.get('withdrawalAmount')?.hasError('exceedsBalance')">
            Amount cannot exceed your current balance.
          </mat-error>
          
          <!-- Real-Time "Too Much" Alert -->
          <mat-error *ngIf="withdrawalForm.get('withdrawalAmount')?.hasError('exceedsNinetyPercent')">
            Amount is too much! It cannot exceed 90% of your balance.
          </mat-error>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()" [disabled]="isSubmitting">Cancel</button>
      <button mat-raised-button color="primary" [disabled]="withdrawalForm.invalid || isSubmitting" (click)="onSubmit()">
        Submit Notice
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .form-container { display: flex; flex-direction: column; margin-top: 16px; }
    mat-form-field { width: 100%; }
    .alert-info {
      display: flex;
      align-items: center;
      gap: 10px;
      background: #eff6ff;
      border-left: 4px solid #3b82f6;
      color: #1e40af;
      padding: 10px;
      border-radius: 4px;
      margin-top: 10px;
      font-size: 14px;
    }
  `]
})
export class WithdrawalDialogComponent {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<WithdrawalDialogComponent>);
  
  withdrawalForm: FormGroup;
  isSubmitting: boolean = false; // Controls the "request is being done" view state

  constructor(@Inject(MAT_DIALOG_DATA) public data: { product: Product }) {
    this.withdrawalForm = this.fb.group({
      withdrawalAmount: [
        '', 
        [
          Validators.required, 
          Validators.min(0.01), 
          this.balanceValidator(this.data.product.currentBalance),
          this.ninetyPercentValidator(this.data.product.currentBalance) // New threshold filter
        ]
      ]
  });
  }

  getMaxAllowed(): number {
    return this.data.product.currentBalance * 0.90;
  }

  private balanceValidator(balance: number) {
    return (control: any) => {
      if (control.value && control.value > balance) {
        return { exceedsBalance: true };
      }
      return null;
    };
  }

  // Real-time calculation check to stop user from entering "too much"
  private ninetyPercentValidator(balance: number) {
    return (control: any) => {
      const maxAllowed = balance * 0.90;
      if (control.value && control.value > maxAllowed) {
        return { exceedsNinetyPercent: true };
      }
      return null;
    };
  }

  onCancel() {
    this.dialogRef.close();
  }

  onSubmit() {
    if (this.withdrawalForm.valid) {
      this.isSubmitting = true; // Flashes the "request is being done" layout notice immediately
      
      this.dialogRef.close({
        productId: this.data.product.id,
        amount: this.withdrawalForm.value.withdrawalAmount
      });
    }
  }
}