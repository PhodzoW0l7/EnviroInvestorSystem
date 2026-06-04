package com.enviro.assessment.junior.phodzo.nagana.dto;

import java.math.BigDecimal;

public class ProductResponseDto {

    private Long id;
    private String name;
    private String type;
    private BigDecimal currentBalance;

    public ProductResponseDto(Long id, String name,String type, BigDecimal currentBalance){
        this.id=id;
        this.name=name;
        this.type=type;
        this.currentBalance=currentBalance;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type=type;
    }
    public BigDecimal getCurrentBalance(){
        return currentBalance;
    }
    public void setCurrentBalance(BigDecimal currentBalance){
        this.currentBalance=currentBalance;
    }
}
