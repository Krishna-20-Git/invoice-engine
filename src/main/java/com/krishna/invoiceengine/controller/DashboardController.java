package com.krishna.invoiceengine.controller;

import com.krishna.invoiceengine.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final InvoiceRepository invoiceRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        long total = invoiceRepository.count();
        long done = invoiceRepository.findByStatus("DONE").size();
        long processing = invoiceRepository.findByStatus("PROCESSING").size();
        long pending = invoiceRepository.findByStatus("PENDING").size();

        return ResponseEntity.ok(Map.of(
                "totalInvoices", total,
                "done", done,
                "processing", processing,
                "pending", pending,
                "successRate", total > 0 ? (done * 100 / total) + "%" : "0%"
        ));
    }
}