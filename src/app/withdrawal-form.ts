import { Component, Inject, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
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
    MatButtonModule
  ],
  template: `
    <h2 mat-dialog-title>Withdrawal Notice</h2>
    <mat-dialog-content>
      <p>Product: <strong>{{ data.product.name }}</strong> ({{ data.product.type }})</p>
      <p>Available Balance: <strong>{{ data.product.currentBalance | currency:'ZAR':'R' }}</strong></p>

      <form [formGroup]="withdrawalForm" class="form-container">
        <mat-form-field appearance="outline">
          <mat-label>Withdrawal Amount</mat-label>
          <input matInput type="number" formControlName="withdrawalAmount" placeholder="Enter amount">
          <mat-error *ngIf="withdrawalForm.get('withdrawalAmount')?.hasError('required')">
            Amount is required.
          </mat-error>
          <mat-error *ngIf="withdrawalForm.get('withdrawalAmount')?.hasError('min')">
            Amount must be greater than zero.
          </mat-error>
          <mat-error *ngIf="withdrawalForm.get('withdrawalAmount')?.hasError('exceedsBalance')">
            Amount cannot exceed your current balance.
          </mat-error>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancel</button>
      <button mat-raised-button color="primary" [disabled]="withdrawalForm.invalid" (click)="onSubmit()">
        Submit Notice
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .form-container { display: flex; flex-direction: column; margin-top: 16px; }
    mat-form-field { width: 100%; }
  `]
})
export class WithdrawalDialogComponent {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<WithdrawalDialogComponent>);
  
  withdrawalForm: FormGroup;

  constructor(@Inject(MAT_DIALOG_DATA) public data: { product: Product }) {
    this.withdrawalForm = this.fb.group({
      withdrawalAmount: [
        '', 
        [Validators.required, Validators.min(0.01), this.balanceValidator(this.data.product.currentBalance)]
      ]
    });
  }

  // Custom validator to stop user from withdrawing more than available balance
  private balanceValidator(balance: number) {
    return (control: any) => {
      if (control.value && control.value > balance) {
        return { exceedsBalance: true };
      }
      return null;
    };
  }

  onCancel() {
    this.dialogRef.close();
  }

  onSubmit() {
    if (this.withdrawalForm.valid) {
      // Send data back to parent component
      this.dialogRef.close({
  productId: this.data.product.id,
  amount: this.withdrawalForm.value.withdrawalAmount
      });
    }
  }
}
