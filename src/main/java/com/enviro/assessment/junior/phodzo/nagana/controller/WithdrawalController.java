package com.enviro.assessment.junior.phodzo.nagana.controller;

import com.enviro.assessment.junior.phodzo.nagana.dto.InvestorDto;
import com.enviro.assessment.junior.phodzo.nagana.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.phodzo.nagana.model.WithdrawalNotice;
import com.enviro.assessment.junior.phodzo.nagana.service.WithdrawalService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WithdrawalController {
    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService){
        this.withdrawalService=withdrawalService;
    }

    @GetMapping("/investors/{id}/portfolio")
    public ResponseEntity<InvestorDto> getInvestorPortfolio(@PathVariable Long id){
        return ResponseEntity.ok(withdrawalService.getInvestorPortfolio(id));
    }

    @PostMapping("/withdrawals")
    public ResponseEntity<WithdrawalNotice> createWithdrawalNotice(@Valid @RequestBody WithdrawalRequestDto requestDto){
        WithdrawalNotice Notice= withdrawalService.createWithdrawalNotice(requestDto);
        return ResponseEntity.ok(Notice);
    }
    @GetMapping("/products/{productId}/export")
    public ResponseEntity<Resource> exportCsvFile(@PathVariable Long productId,@RequestParam(required = false) String status){
        ByteArrayInputStream csvStream=withdrawalService.exportWithdrawalHistoryCsv(productId,status);
        Resource fileResource = new InputStreamResource(csvStream);
        String fileName="withdrawal_statement_product_"+productId+".csv";
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename="+fileName)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(fileResource);
    }
}
