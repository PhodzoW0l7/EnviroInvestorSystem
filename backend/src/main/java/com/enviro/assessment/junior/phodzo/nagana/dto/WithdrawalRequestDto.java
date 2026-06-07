package com.enviro.assessment.junior.phodzo.nagana.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

//This is an inbound dto
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
        //Instead of sending an entire entity over a network,
        // the client only needs to send two specific pieces of data:
        // the target product's ID and the cash amount.
    }

}
