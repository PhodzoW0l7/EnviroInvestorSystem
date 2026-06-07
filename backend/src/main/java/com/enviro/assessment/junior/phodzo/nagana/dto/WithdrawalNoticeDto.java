package com.enviro.assessment.junior.phodzo.nagana.dto;

import com.enviro.assessment.junior.phodzo.nagana.model.WithdrawalNotice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WithdrawalNoticeDto {
        private Long id;
        private BigDecimal withdrawalAmount;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getWithdrawalAmount() {
        return withdrawalAmount;
    }

    public void setWithdrawalAmount(BigDecimal withdrawalAmount) {
        this.withdrawalAmount = withdrawalAmount;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    private String status;
        private LocalDateTime requestDate;
        private Long productId;
        private String productName;

        // constructor
        public WithdrawalNoticeDto(WithdrawalNotice notice) {
            this.id               = notice.getId();
            this.withdrawalAmount = notice.getWithdrawalAmount();
            this.status           = notice.getStatus();
            this.requestDate      = notice.getRequestDate();
            this.productId        = notice.getProduct().getId();
            this.productName      = notice.getProduct().getName();
        }


    }
