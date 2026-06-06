package com.enviro.assessment.junior.phodzo.nagana.util;

import com.enviro.assessment.junior.phodzo.nagana.model.Investor;
import com.enviro.assessment.junior.phodzo.nagana.model.Product;
import com.enviro.assessment.junior.phodzo.nagana.model.ProductType;
import com.enviro.assessment.junior.phodzo.nagana.repository.InvestorRepository;
import com.enviro.assessment.junior.phodzo.nagana.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements org.springframework.boot.CommandLineRunner {
    private final InvestorRepository investorRepository;
    private final ProductRepository productRepository;

    public DataInitializer(InvestorRepository investorRepository, ProductRepository productRepository) {
        this.investorRepository = investorRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(java.lang.String[] args) throws java.lang.Exception {
        // 1. Create a Retirement Investor (Under 65 to test your age rule restriction)
        Investor youngInvestor = new Investor();
        youngInvestor.setName("Sipho");
        youngInvestor.setSurname("Dlamini");
        youngInvestor.setEmail("sipho@enviro.co.za");
        youngInvestor.setDateOfBirth(LocalDate.of(1995, 6, 15)); // Age: ~31 (Should fail retirement withdrawal)
        investorRepository.save(youngInvestor);

        // Assign an active savings balance to Sipho
        Product siphoSavings = new Product();
        siphoSavings.setName("Tax-Free Savings Account");
        siphoSavings.setType(ProductType.SAVINGS);
        siphoSavings.setCurrentBalance(new BigDecimal("50000.00")); // R50,000.00
        siphoSavings.setInvestor(youngInvestor);
        productRepository.save(siphoSavings);

        Product siphoRetirement = new Product();
        siphoRetirement.setName("Corporate Retirement Fund");
        siphoRetirement.setType(ProductType.RETIREMENT);
        siphoRetirement.setCurrentBalance(new BigDecimal("500000.00")); // R500,000.00
        siphoRetirement.setInvestor(youngInvestor);
        productRepository.save(siphoRetirement);

        // 2. Create an Older Investor (Over 65 to test successful retirement criteria)
        Investor seniorInvestor = new Investor();
        seniorInvestor.setName("John");
        seniorInvestor.setSurname("Smith");
        seniorInvestor.setEmail("john.smith@enviro.co.za");
        seniorInvestor.setDateOfBirth(LocalDate.of(1955, 3, 10)); // Age: ~71 (Should pass retirement withdrawal)
        investorRepository.save(seniorInvestor);

        Product johnRetirement = new Product();
        johnRetirement.setName("Personal Annuity Plan");
        johnRetirement.setType(ProductType.RETIREMENT);
        johnRetirement.setCurrentBalance(new BigDecimal("1200000.00")); // R1.2 Million
        johnRetirement.setInvestor(seniorInvestor);
        productRepository.save(johnRetirement);

        // 3. Middle-aged investor with only savings (tests savings withdrawal freely)
        Investor middleInvestor = new Investor();
        middleInvestor.setName("Priya");
        middleInvestor.setSurname("Naidoo");
        middleInvestor.setEmail("priya.naidoo@enviro.co.za");
        middleInvestor.setDateOfBirth(LocalDate.of(1980, 9, 22)); // Age: ~45
        investorRepository.save(middleInvestor);

        Product priyaSavings = new Product();
        priyaSavings.setName("Unit Trust Fund");
        priyaSavings.setType(ProductType.SAVINGS);
        priyaSavings.setCurrentBalance(new BigDecimal("250000.00"));
        priyaSavings.setInvestor(middleInvestor);
        productRepository.save(priyaSavings);

        System.out.println(">>> Enviro365 Database Initializer complete: Mock portfolios populated successfully! <<<");
    }
}
