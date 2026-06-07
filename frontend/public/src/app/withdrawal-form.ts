import { Component, Inject, inject,ChangeDetectorRef  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PortfolioService } from './portfolio.service'; // ADD THIS
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

      <!-- Processing spinner -->
      <div *ngIf="isSubmitting && !serverError" class="alert-info">
    <mat-spinner diameter="20"></mat-spinner>
    <span>Your request is being processed. Please wait...</span>
    </div>

      <!-- Backend error banner — stays visible so user can correct and retry -->
        <div *ngIf="serverError" class="alert-error">
      <span>⚠ {{ serverError }}</span>
        </div>

      <form [formGroup]="withdrawalForm" class="form-container">
        <mat-form-field appearance="outline">
          <mat-label>Withdrawal Amount</mat-label>
          <input matInput type="number" formControlName="withdrawalAmount" 
                 placeholder="Enter amount" [readonly]="isSubmitting">
          <mat-error *ngIf="withdrawalForm.get('withdrawalAmount')?.hasError('required')">
            Amount is required.
          </mat-error>
          <mat-error *ngIf="withdrawalForm.get('withdrawalAmount')?.hasError('min')">
            Amount must be greater than zero.
          </mat-error>
          <mat-error *ngIf="withdrawalForm.get('withdrawalAmount')?.hasError('exceedsBalance')">
            Amount cannot exceed your current balance.
          </mat-error>
          <mat-error *ngIf="withdrawalForm.get('withdrawalAmount')?.hasError('exceedsNinetyPercent')">
            Amount is too much! It cannot exceed 90% of your balance.
          </mat-error>
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()" [disabled]="isSubmitting">Cancel</button>
      <button mat-raised-button color="primary" 
              [disabled]="withdrawalForm.invalid || isSubmitting" 
              (click)="onSubmit()">
        Submit Notice
      </button>
    </mat-dialog-actions>
  `,
  styles: [`@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;600;700;800&display=swap');

:host {
  display: block;
  font-family: 'Nunito', sans-serif;
}

h2[mat-dialog-title] {
  font-size: 1.5rem;
  font-weight: 800;
  color: #1e4620;
  margin: 0;
  padding: 24px 24px 0;
}

mat-dialog-content {
  padding: 16px 24px !important;
}

mat-dialog-content p {
  margin: 0 0 8px;
  font-size: 0.875rem;
  color: #2d6a2f;
}

mat-dialog-content p strong {
  color: #1e4620;
  font-weight: 700;
}

.alert-info, .alert-error {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  border-radius: 8px;
  margin: 16px 0;
  font-size: 0.8125rem;
  font-weight: 600;
}

.alert-info {
  background: #e8f5e9;
  border-left: 3px solid #4caf50;
  color: #1e4620;
}

.alert-error {
  background: #ffebee;
  border-left: 3px solid #f44336;
  color: #c62828;
}

mat-form-field {
  width: 100%;
}

mat-dialog-actions {
  padding: 16px 24px 24px;
  gap: 12px;
  border-top: 1px solid #e8f5e9;
}

button[mat-raised-button][color="primary"] {
  background: #4caf50 !important;
  color: white !important;
  font-weight: 700;
}

button[mat-raised-button][color="primary"]:hover {
  background: #388e3c !important;
}

button:disabled {
  opacity: 0.5;
}

@media (max-width: 600px) {
  h2[mat-dialog-title] { padding: 20px 20px 0; font-size: 1.25rem; }
  mat-dialog-content { padding: 12px 20px !important; }
  mat-dialog-actions { padding: 12px 20px 20px; flex-direction: column-reverse; }
  button { width: 100%; }
}`]
})
export class WithdrawalDialogComponent {
  private fb               = inject(FormBuilder);
  private dialogRef        = inject(MatDialogRef<WithdrawalDialogComponent>);
  private portfolioService = inject(PortfolioService); // ADD THIS
  private cdr              = inject(ChangeDetectorRef); // ADD THIS

  withdrawalForm: FormGroup;
  isSubmitting = false;
  serverError: string | null = null; // ADD THIS

  constructor(@Inject(MAT_DIALOG_DATA) public data: { product: Product }) {
    this.withdrawalForm = this.fb.group({
      withdrawalAmount: [
        '',
        [
          Validators.required,
          Validators.min(0.01),
          this.balanceValidator(this.data.product.currentBalance),
          this.ninetyPercentValidator(this.data.product.currentBalance)
        ]
      ]
    });
  }

  getMaxAllowed(): number {
    return this.data.product.currentBalance * 0.90;
  }

  private balanceValidator(balance: number) {
    return (control: any) =>
      control.value && control.value > balance ? { exceedsBalance: true } : null;
  }

  private ninetyPercentValidator(balance: number) {
    return (control: any) => {
      const maxAllowed = balance * 0.90;
      return control.value && control.value > maxAllowed ? { exceedsNinetyPercent: true } : null;
    };
  }

  onCancel() {
    this.dialogRef.close(null);
  }

  onSubmit() {
    if (this.withdrawalForm.invalid || this.isSubmitting) return;

    this.isSubmitting = true;
    this.serverError = null; // Clear previous error on each new attempt

    this.portfolioService.createWithdrawal({
      productId: this.data.product.id,
      amount: this.withdrawalForm.value.withdrawalAmount
    }).subscribe({
     next: (res) => {
  console.log(' SUCCESS', res);
  this.dialogRef.close(true);
},error: (err) => {
  try {
    const parsed = JSON.parse(err.error);
    this.serverError = parsed.error;
  } catch {
    this.serverError = 'An unexpected error occurred. Please try again.';
  }
  this.isSubmitting = false;
  this.cdr.detectChanges();
}
    });
  }
}