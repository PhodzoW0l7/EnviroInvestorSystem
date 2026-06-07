package com.enviro.assessment.junior.phodzo.nagana.service;

import com.enviro.assessment.junior.phodzo.nagana.dto.InvestorDto;
import com.enviro.assessment.junior.phodzo.nagana.dto.ProductResponseDto;
import com.enviro.assessment.junior.phodzo.nagana.dto.WithdrawalNoticeDto;
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
    //here were using springs dependency injection to inject 3 data repositories
    public WithdrawalService(InvestorRepository investorRepository, ProductRepository productRepository,
                             WithdrawalNoticeRepository withdrawalNoticeRepository){
        this.investorRepository=investorRepository;
        this.productRepository=productRepository;
        this.withdrawalNoticeRepository=withdrawalNoticeRepository;
    }
    //This method is fetching the investor and the products which the investor has
    public InvestorDto getInvestorPortfolio(Long investorId) {
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new BusinessLogicValidationException("Investor not found with ID: " + investorId));
        int age = Period.between(investor.getDateOfBirth(), LocalDate.now()).getYears();
        List<ProductResponseDto> productDtos = investor.getProducts().stream()
                .map(p -> new ProductResponseDto(p.getId(), p.getName(), p.getType().name(), p.getCurrentBalance()))
                .collect(Collectors.toList());
        //here this is where it sets the investor into a new Object
        return new InvestorDto(investor.getId(), investor.getName(), investor.getSurname(),
                investor.getEmail(), age, productDtos);
    }
    //withdrawal notice method
    @Transactional
    public WithdrawalNoticeDto createWithdrawalNotice(WithdrawalRequestDto request) {
        // goes and fetches a new product using its ID
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessLogicValidationException("Product not found with ID: " + request.getProductId()));

        Investor investor = product.getInvestor();
        BigDecimal requestedAmount = request.getAmount();
        BigDecimal currentBalance = product.getCurrentBalance();

        // validation for age withdrawal limit
        if (product.getType() == ProductType.RETIREMENT) {
            int age = Period.between(investor.getDateOfBirth(), LocalDate.now()).getYears();
            if (age <= 65) {
                throw new BusinessLogicValidationException(
                        "Retirement fund withdrawal restricted. Investor age is " + age + ". Must be older than 65.");
            }
        }

        // checks whether the user does not withdraw more than what they have
        if (requestedAmount.compareTo(currentBalance) > 0) {
            throw new BusinessLogicValidationException(
                    "Insufficient funds. Requested: R" + requestedAmount + ", Available: R" + currentBalance);
        }

        // checks whether the user is not withdrawing more than the 90% of the account
        BigDecimal maxAllowed = currentBalance.multiply(new BigDecimal("0.90"));
        if (requestedAmount.compareTo(maxAllowed) > 0) {
            throw new BusinessLogicValidationException(
                    "Amount exceeds maximum allowed withdrawal of 90% (R" + maxAllowed.setScale(2) + ").");
        }

        // sets the notice as successful
        product.setCurrentBalance(currentBalance.subtract(requestedAmount));
        productRepository.save(product);

        WithdrawalNotice notice = new WithdrawalNotice();
        notice.setProduct(product);
        notice.setWithdrawalAmount(requestedAmount);
        notice.setRequestDate(LocalDateTime.now());
        notice.setStatus("SUCCESS");

        return new WithdrawalNoticeDto(withdrawalNoticeRepository.save(notice)); // wrapped here
    }

    public List<WithdrawalNoticeDto> getWithdrawalHistory(Long productId) {
        return withdrawalNoticeRepository.findByProductId(productId)
                .stream()
                .map(WithdrawalNoticeDto::new)
                .collect(Collectors.toList());
    }

    public ByteArrayInputStream exportWithdrawalHistoryCsv(Long productId, String statusFilter,
                                                           LocalDate fromDate, LocalDate toDate) {
        List<WithdrawalNotice> notices = withdrawalNoticeRepository.findByProductId(productId);

        // Filter by status
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            notices = notices.stream()
                    .filter(n -> n.getStatus().equalsIgnoreCase(statusFilter.trim()))
                    .collect(Collectors.toList());
        }

        // Filter by date range
        if (fromDate != null) {
            notices = notices.stream()
                    .filter(n -> !n.getRequestDate().toLocalDate().isBefore(fromDate))
                    .collect(Collectors.toList());
        }
        if (toDate != null) {
            notices = notices.stream()
                    .filter(n -> !n.getRequestDate().toLocalDate().isAfter(toDate))
                    .collect(Collectors.toList());
        }
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("Notice ID", "Product ID", "Product Name",
                        "Investor Name", "Investor Surname",
                        "Withdrawal Amount (ZAR)", "Request Date", "Status")
                .build();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {

            for (WithdrawalNotice notice : notices) {
                csvPrinter.printRecord(
                        notice.getId(),
                        notice.getProduct().getId(),
                        notice.getProduct().getName(),
                        notice.getProduct().getInvestor().getName(),
                        notice.getProduct().getInvestor().getSurname(),
                        notice.getWithdrawalAmount(),
                        notice.getRequestDate().toString(),
                        notice.getStatus()
                );
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to export CSV: " + e.getMessage());
        }
    }

}
