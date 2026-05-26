package com.krishna.invoiceengine.service;

import com.krishna.invoiceengine.kafka.InvoiceProducer;
import com.krishna.invoiceengine.model.Invoice;
import com.krishna.invoiceengine.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceProducer invoiceProducer;

    public Invoice createInvoice(String companyName, Double amount) {
        Invoice invoice = new Invoice();
        invoice.setCompanyName(companyName);
        invoice.setAmount(amount);
        Invoice saved = invoiceRepository.save(invoice);
        invoiceProducer.sendInvoice(saved.getId());
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