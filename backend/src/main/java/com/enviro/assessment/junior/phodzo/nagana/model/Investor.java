package com.enviro.assessment.junior.phodzo.nagana.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

//This marks my class as a database model
@Entity
@Table(name = "investors")
public class Investor {

    //Declares the id as the primary key
    @Id
    //This configures id to auto increment for new ids added
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //database constraint enforces this field can never be empty
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    @Column(nullable = false)
    private String email;
//    This annotation defines a one-to-many relationship between two database entities
@OneToMany(mappedBy = "investor", cascade = CascadeType.ALL)
private List<Product> products;

//getters and setters
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
