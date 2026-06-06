package com.enviro.assessment.junior.phodzo.nagana.controller;

import com.enviro.assessment.junior.phodzo.nagana.dto.InvestorDto;
import com.enviro.assessment.junior.phodzo.nagana.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.phodzo.nagana.model.WithdrawalNotice;
import com.enviro.assessment.junior.phodzo.nagana.service.WithdrawalService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class WithdrawalController {
    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService){
        this.withdrawalService=withdrawalService;
    }

    @GetMapping("/investors/{id}/portfolio")
    public ResponseEntity<InvestorDto> getInvestorPortfolio(@PathVariable Long id) {
        return ResponseEntity.ok(withdrawalService.getInvestorPortfolio(id));
    }

    @PostMapping("/withdrawals")
    public ResponseEntity<WithdrawalNotice> createWithdrawalNotice(
            @Valid @RequestBody WithdrawalRequestDto requestDto) {
        WithdrawalNotice notice = withdrawalService.createWithdrawalNotice(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(notice);
    }
        @GetMapping("/products/{productId}/export")
    public ResponseEntity<Resource> exportCsvFile(
            @PathVariable Long productId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        ByteArrayInputStream csvStream = withdrawalService.exportWithdrawalHistoryCsv(
                productId, status, fromDate, toDate);

        Resource fileResource = new InputStreamResource(csvStream);
        String fileName = "withdrawal_statement_product_" + productId + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(fileResource);
    }
}
