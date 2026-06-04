package com.enviro.assessment.junior.phodzo.nagana.dto;

import java.util.List;

public class InvestorDto {

    private Long investorId;
    private String fullName;
    private int age;
    private List<ProductResponseDto> products;

    public InvestorDto(Long investorId,String fullName,int age,List<ProductResponseDto> products){
        this.investorId=investorId;
        this.fullName=fullName;
        this.age=age;
        this.products=products;
    }
    public Long getInvestorId() {
        return investorId;
    }

    public void setInvestorId(Long investorId) {
        this.investorId = investorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<ProductResponseDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductResponseDto> products) {
        this.products = products;
    }
}
