package com.enviro.assessment.junior.phodzo.nagana.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WithdrawalServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private WithdrawalNoticeRepository withdrawalNoticeRepository;
    @Mock
    private InvestorRepository investorRepository;

    @InjectMocks
    private WithdrawalService withdrawalService;

    private Product savingsProduct;
    private Product retirementProduct;
    private Investor youngInvestor;
    private Investor oldInvestor;

    @BeforeEach
    void setUp(){
        youngInvestor = new Investor();
        youngInvestor.setId(1L);
        youngInvestor.setName("John");
        youngInvestor.setSurname("Smith");
        youngInvestor.setDateOfBirth(LocalDate.now().minusYears(30));

        oldInvestor = new Investor();
        oldInvestor.setId(2L);
        oldInvestor.setName("Sipho");
        oldInvestor.setSurname("Dlamini");
        oldInvestor.setDateOfBirth(LocalDate.now().minusYears(70));

        // Savings product for young investor
        savingsProduct = new Product();
        savingsProduct.setId(1L);
        savingsProduct.setName("Savings Account");
        savingsProduct.setType(ProductType.SAVINGS);
        savingsProduct.setCurrentBalance(new BigDecimal("10000.00"));
        savingsProduct.setInvestor(youngInvestor);

        // Retirement product linked to young investor
        retirementProduct = new Product();
        retirementProduct.setId(2L);
        retirementProduct.setName("Retirement Fund");
        retirementProduct.setType(ProductType.RETIREMENT);
        retirementProduct.setCurrentBalance(new BigDecimal("500000.00"));
        retirementProduct.setInvestor(youngInvestor);
    }

    @Test
    void shouldProcessWithdrawalSuccssfully(){

        WithdrawalRequestDto request=new WithdrawalRequestDto();
        request.setProductId(1L);
        request.setAmount(new BigDecimal("5000.00"));

        WithdrawalNotice savedNotice=new WithdrawalNotice();
        savedNotice.setId(1L);
        savedNotice.setProduct(savingsProduct);
        savedNotice.setWithdrawalAmount(new BigDecimal("5000.00"));
        savedNotice.setStatus("SUCCESS");
        savedNotice.setRequestDate(LocalDateTime.now());

        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsProduct));
        when(withdrawalNoticeRepository.save(any())).thenReturn(savedNotice);

        WithdrawalNoticeDto result = withdrawalService.createWithdrawalNotice(request);

        // Assert
        assertThat(result.getStatus()).isEqualTo("SUCCESS");
        assertThat(result.getWithdrawalAmount()).isEqualByComparingTo("5000.00");
        verify(productRepository).save(savingsProduct); // balance was saved
    }

    @Test
    void shouldReduceProductBalanceAfterWithdrawal() {
        // Arrange
        WithdrawalRequestDto request = new WithdrawalRequestDto();
        request.setProductId(1L);
        request.setAmount(new BigDecimal("3000.00"));

        WithdrawalNotice savedNotice = new WithdrawalNotice();
        savedNotice.setId(1L);
        savedNotice.setProduct(savingsProduct);
        savedNotice.setWithdrawalAmount(new BigDecimal("3000.00"));
        savedNotice.setStatus("SUCCESS");
        savedNotice.setRequestDate(LocalDateTime.now());

        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsProduct));
        when(withdrawalNoticeRepository.save(any())).thenReturn(savedNotice);

        // Act
        withdrawalService.createWithdrawalNotice(request);

        // Assert — balance should be 10000 - 3000 = 7000
        assertThat(savingsProduct.getCurrentBalance()).isEqualByComparingTo("7000.00");
    }

    // ─── Product Not Found ────────────────────────────────────────────────────

    @Test
    void shouldThrowWhenProductNotFound() {
        // Arrange
        WithdrawalRequestDto request = new WithdrawalRequestDto();
        request.setProductId(99L);
        request.setAmount(new BigDecimal("1000.00"));

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> withdrawalService.createWithdrawalNotice(request))
                .isInstanceOf(BusinessLogicValidationException.class)
                .hasMessageContaining("Product not found with ID: 99");
    }

    // ─── Age Restriction ──────────────────────────────────────────────────────

    @Test
    void shouldThrowWhenInvestorTooYoungForRetirement() {
        // Arrange — young investor (age 30) trying to withdraw from RETIREMENT
        WithdrawalRequestDto request = new WithdrawalRequestDto();
        request.setProductId(2L);
        request.setAmount(new BigDecimal("1000.00"));

        when(productRepository.findById(2L)).thenReturn(Optional.of(retirementProduct));

        // Act & Assert
        assertThatThrownBy(() -> withdrawalService.createWithdrawalNotice(request))
                .isInstanceOf(BusinessLogicValidationException.class)
                .hasMessageContaining("Must be older than 65");
    }

    @Test
    void shouldAllowRetirementWithdrawalWhenInvestorOlderThan65() {
        // Arrange — old investor (age 70) on retirement product
        retirementProduct.setInvestor(oldInvestor);

        WithdrawalRequestDto request = new WithdrawalRequestDto();
        request.setProductId(2L);
        request.setAmount(new BigDecimal("1000.00"));

        WithdrawalNotice savedNotice = new WithdrawalNotice();
        savedNotice.setId(1L);
        savedNotice.setProduct(retirementProduct);
        savedNotice.setWithdrawalAmount(new BigDecimal("1000.00"));
        savedNotice.setStatus("SUCCESS");
        savedNotice.setRequestDate(LocalDateTime.now());

        when(productRepository.findById(2L)).thenReturn(Optional.of(retirementProduct));
        when(withdrawalNoticeRepository.save(any())).thenReturn(savedNotice);

        // Act & Assert — should not throw
        assertThatNoException().isThrownBy(() -> withdrawalService.createWithdrawalNotice(request));
    }

    // ─── Insufficient Funds ───────────────────────────────────────────────────

    @Test
    void shouldThrowWhenAmountExceedsBalance() {
        // Arrange — requesting more than the R10,000 balance
        WithdrawalRequestDto request = new WithdrawalRequestDto();
        request.setProductId(1L);
        request.setAmount(new BigDecimal("15000.00"));

        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsProduct));

        // Act & Assert
        assertThatThrownBy(() -> withdrawalService.createWithdrawalNotice(request))
                .isInstanceOf(BusinessLogicValidationException.class)
                .hasMessageContaining("Insufficient funds");
    }

    // ─── 90% Cap ──────────────────────────────────────────────────────────────

    @Test
    void shouldThrowWhenAmountExceedsNinetyPercent() {
        // Arrange — 90% of R10,000 is R9,000 — requesting R9,500 should fail
        WithdrawalRequestDto request = new WithdrawalRequestDto();
        request.setProductId(1L);
        request.setAmount(new BigDecimal("9500.00"));

        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsProduct));

        // Act & Assert
        assertThatThrownBy(() -> withdrawalService.createWithdrawalNotice(request))
                .isInstanceOf(BusinessLogicValidationException.class)
                .hasMessageContaining("90%");
    }

    @Test
    void shouldAllowWithdrawalAtExactlyNinetyPercent() {
        // Arrange — exactly R9,000 (90% of R10,000) should pass
        WithdrawalRequestDto request = new WithdrawalRequestDto();
        request.setProductId(1L);
        request.setAmount(new BigDecimal("9000.00"));

        WithdrawalNotice savedNotice = new WithdrawalNotice();
        savedNotice.setId(1L);
        savedNotice.setProduct(savingsProduct);
        savedNotice.setWithdrawalAmount(new BigDecimal("9000.00"));
        savedNotice.setStatus("SUCCESS");
        savedNotice.setRequestDate(LocalDateTime.now());

        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsProduct));
        when(withdrawalNoticeRepository.save(any())).thenReturn(savedNotice);

        // Act & Assert — exact 90% boundary should not throw
        assertThatNoException().isThrownBy(() -> withdrawalService.createWithdrawalNotice(request));
    }

}
