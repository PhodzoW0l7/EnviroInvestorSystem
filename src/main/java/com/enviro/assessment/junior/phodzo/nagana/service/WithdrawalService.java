package com.enviro.assessment.junior.phodzo.nagana.service;

import com.enviro.assessment.junior.phodzo.nagana.dto.InvestorDto;
import com.enviro.assessment.junior.phodzo.nagana.dto.ProductResponseDto;
import com.enviro.assessment.junior.phodzo.nagana.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.phodzo.nagana.exception.BusinessLogicValidationException;
import com.enviro.assessment.junior.phodzo.nagana.model.Investor;
import com.enviro.assessment.junior.phodzo.nagana.model.Product;
import com.enviro.assessment.junior.phodzo.nagana.model.ProductType;
import com.enviro.assessment.junior.phodzo.nagana.model.WithdrawalNotice;
import com.enviro.assessment.junior.phodzo.nagana.repository.InvestorRepository;
import com.enviro.assessment.junior.phodzo.nagana.repository.ProductRepository;
import com.enviro.assessment.junior.phodzo.nagana.repository.WithdrawalNoticeRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
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
                throw new BusinessLogicValidationException("Investor age is "+age+". Retirement fund withdrawal restricted for age <= 65.");
            }
        }
        if(requestedAmount.compareTo(currentBalance)>0){
            throw new BusinessLogicValidationException("Insufficient funds. Requested: R"+requestedAmount+", Available Balance: R"+currentBalance);
        }
        //Implementation of how the maximum amount of to be withdrawn can only be 90% of the account balance
        BigDecimal maxAllowedWithdrawal=currentBalance.multiply(new BigDecimal("0.90"));
        if(requestedAmount.compareTo(maxAllowedWithdrawal)>0){
            throw new BusinessLogicValidationException("Withdrawal amount exceeds the maximum allowed withdrawal amount of 90% of the account");
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

    public ByteArrayInputStream exportWithdrawalHistoryCsv(Long productId, String statusFilter){
        List<WithdrawalNotice> notices=withdrawalNoticeRepository.findByProductId(productId);
        if(statusFilter !=null && !statusFilter.trim().isEmpty()){
            notices=notices.stream().filter(note-> note.getStatus().equalsIgnoreCase(statusFilter.trim()))
                    .collect(Collectors.toList());
        }
        //Creating a new csv file called format with the below tables
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("Notice ID", "Product ID", "Product Name", "Withdrawal Amount (ZAR)", "Request Date", "Status")
                .build();
        //Creating a new bytearrayoutstream object
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            for (WithdrawalNotice notice : notices) {
                csvPrinter.printRecord(
                        notice.getId(),
                        notice.getProduct().getId(),
                        notice.getProduct().getName(),
                        notice.getWithdrawalAmount(),
                        notice.getRequestDate().toString(),
                        notice.getStatus()
                );
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Fail to import data to CSV file: " + e.getMessage());
        }
    }

}
