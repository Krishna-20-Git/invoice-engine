package com.krishna.invoiceengine.service;

import com.krishna.invoiceengine.kafka.InvoiceProducer;
import com.krishna.invoiceengine.model.Invoice;
import com.krishna.invoiceengine.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulkInvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceProducer invoiceProducer;

    public int processCsvUpload(MultipartFile file) throws Exception {
        List<Invoice> invoices = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                // Skip header row
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] fields = line.split(",");
                if (fields.length < 2) continue;

                Invoice invoice = new Invoice();
                invoice.setCompanyName(fields[0].trim());
                invoice.setAmount(Double.parseDouble(fields[1].trim()));
                invoices.add(invoice);
            }
        }

        // Bulk save all invoices to database
        List<Invoice> saved = invoiceRepository.saveAll(invoices);
        log.info("Bulk saved {} invoices to database", saved.size());

        // Send each invoice to Kafka for async processing
        saved.forEach(invoice -> invoiceProducer.sendInvoice(invoice.getId()));
        log.info("Sent {} invoices to Kafka queue", saved.size());

        return saved.size();
    }
}