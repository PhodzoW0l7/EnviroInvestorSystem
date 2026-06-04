package com.enviro.assessment.junior.phodzo.nagana.service;

import com.enviro.assessment.junior.phodzo.nagana.dto.InvestorDto;
import com.enviro.assessment.junior.phodzo.nagana.dto.ProductResponseDto;
import com.enviro.assessment.junior.phodzo.nagana.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.phodzo.nagana.model.Investor;
import com.enviro.assessment.junior.phodzo.nagana.model.Product;
import com.enviro.assessment.junior.phodzo.nagana.model.ProductType;
import com.enviro.assessment.junior.phodzo.nagana.model.WithdrawalNotice;
import com.enviro.assessment.junior.phodzo.nagana.repository.InvestorRepository;
import com.enviro.assessment.junior.phodzo.nagana.repository.ProductRepository;
import com.enviro.assessment.junior.phodzo.nagana.repository.WithdrawalNoticeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WithdrawalService {

    private final InvestorRepository investorRepository;
    private final ProductRepository productRepository;
    private final WithdrawalNoticeRepository withdrawalNoticeRepository;
    //constructor
    public WithdrawalService(InvestorRepository investorRepository, ProductRepository productRepository,
                             WithdrawalNoticeRepository withdrawalNoticeRepository){
        this.investorRepository=investorRepository;
        this.productRepository=productRepository;
        this.withdrawalNoticeRepository=withdrawalNoticeRepository;
    }

    public InvestorDto getInvestorPortfolio(Long investorId){
        //Getting the investor information by his/her ID
        Investor investor=investorRepository.findById(investorId)
                .orElseThrow(()-> new RuntimeException("Investor was not found with ID: "+investorId));
        int age = Period.between(investor.getDateOfBirth(), LocalDate.now()).getYears();
        String fullName=investor.getName()+" "+investor.getSurname();

        List<ProductResponseDto> productDtos= investor.getProducts().stream().map(pro ->
                new ProductResponseDto(pro.getId(),pro.getName(),pro.getType().name(),pro.getCurrentBalance()))
                .collect(Collectors.toList());
        return new InvestorDto(investor.getId(),fullName,age,productDtos);
    }
    //withdrawal notice method
    @Transactional
    public WithdrawalNotice createWithdrawalNotice(WithdrawalRequestDto request){
        Product product=productRepository.findById(request.getProductId()).
                orElseThrow(()-> new RuntimeException("Product was not found with Id: "+request.getProductId()));
        Investor investor=product.getInvestor();
        BigDecimal requestedAmount=request.getAmount();
        BigDecimal currentBalance=product.getCurrentBalance();
//if statement to check if the
        if(product.getType()== ProductType.RETIREMENT){
            int age=Period.between(investor.getDateOfBirth(),LocalDate.now()).getYears();
            if (age<=65){
                throw new RuntimeException("Investor age is "+age+". Retirement fund withdrawal restricted for age <= 65.");
            }
        }
        if(requestedAmount.compareTo(currentBalance)>0){
            throw new RuntimeException("Insufficient funds. Requested: R"+requestedAmount+", Available Balance: R"+currentBalance);
        }
        //Implementation of how the maximum amount of to be withdrawn can only be 90% of the account balance
        BigDecimal maxAllowedWithdrawal=currentBalance.multiply(new BigDecimal("0.90"));
        if(requestedAmount.compareTo(maxAllowedWithdrawal)>0){
            throw new RuntimeException("Withdrawal amount exceeds the maximum allowed withdrawal amount of 90% of the account");
        }
        //saving to the Entity instance(product)
        product.setCurrentBalance(currentBalance.subtract(requestedAmount));
        productRepository.save(product);
//withdrawal notice entry / logging the transaction
        WithdrawalNotice notice = new WithdrawalNotice();
        notice.setProduct(product);
        notice.setWithdrawalAmount(requestedAmount);
        notice.setRequestDate(LocalDateTime.now());
        notice.setStatus("SUCCESS");

        return withdrawalNoticeRepository.save(notice);
    }
}
