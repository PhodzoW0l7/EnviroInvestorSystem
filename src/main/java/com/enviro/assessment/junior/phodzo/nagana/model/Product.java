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
