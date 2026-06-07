package com.enviro.assessment.junior.phodzo.nagana.dto;

import java.util.List;

//these are the classes which exposes data to the client
public class InvestorDto {

        private Long investorId;
        private String name;
        private String surname;
        private String email;
        private String fullName;
        private int age;
        private List<ProductResponseDto> products;

        public InvestorDto(Long investorId, String name, String surname,
                           String email, int age, List<ProductResponseDto> products) {
            this.investorId = investorId;
            this.name = name;
            this.surname = surname;
            this.email = email;
            this.fullName = name + " " + surname;
            this.age = age;
            this.products = products;
        }

        public Long getInvestorId() { return investorId; }
        public void setInvestorId(Long investorId) { this.investorId = investorId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSurname() { return surname; }
        public void setSurname(String surname) { this.surname = surname; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        public List<ProductResponseDto> getProducts() { return products; }
        public void setProducts(List<ProductResponseDto> products) { this.products = products; }
    }