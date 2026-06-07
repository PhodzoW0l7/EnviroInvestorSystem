package com.enviro.assessment.junior.phodzo.nagana.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
//Maps this class to a database table called withdrawal_notices
@Table(name = "withdrawal_notices")
public class WithdrawalNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //this just means that there can be multiple withdrawal notices which belong to one product
    @ManyToOne
    //it connects two db tables using its product_id
    @JoinColumn(name = "product_id")
    private Product product;
    //Configures high-accuracy financial data in the database.
    @Column(nullable = false,precision = 15,scale=2)
    private BigDecimal withdrawalAmount;
    @Column(nullable = false)
    private LocalDateTime requestDate;
    @Column(nullable = false)
    private String status;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



}
