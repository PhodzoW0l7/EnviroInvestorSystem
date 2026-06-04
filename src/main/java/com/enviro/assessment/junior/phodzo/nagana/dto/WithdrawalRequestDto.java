package com.enviro.assessment.junior.phodzo.nagana.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class WithdrawalRequestDto {

    @NotNull(message = "Product ID is mandatory")
    private Long productId;
    @NotNull(message = "Withdrawal amount is mandatory")
    @Positive(message = "Withdrawal amount must be greater than zero.")
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

}
