package com.enviro.assessment.junior.phodzo.nagana.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
//    tells the database to automatically handle the creation of unique IDs using its own auto-increment feature
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Investor getInvestor() {
        return investor;
    }

    public void setInvestor(Investor investor) {
        this.investor = investor;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false,precision = 15,scale=2)
    private BigDecimal currentBalance;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "investors_id",nullable = false)
    private Investor investor;

}
