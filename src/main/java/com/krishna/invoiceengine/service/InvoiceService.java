package com.krishna.invoiceengine.service;

import com.krishna.invoiceengine.model.Invoice;
import com.krishna.invoiceengine.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PdfGeneratorService pdfGeneratorService;

    public Invoice createInvoice(String companyName, Double amount) {
        Invoice invoice = new Invoice();
        invoice.setCompanyName(companyName);
        invoice.setAmount(amount);
        Invoice saved = invoiceRepository.save(invoice);
        pdfGeneratorService.processInvoiceAsync(saved);
        return saved;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(String id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + id));
    }

    public List<Invoice> getInvoicesByStatus(String status) {
        return invoiceRepository.findByStatus(status);
    }
}