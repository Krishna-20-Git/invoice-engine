package com.krishna.invoiceengine.kafka;

import com.krishna.invoiceengine.model.Invoice;
import com.krishna.invoiceengine.repository.InvoiceRepository;
import com.krishna.invoiceengine.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers", havingValue = "localhost:9092", matchIfMissing = false)
public class InvoiceConsumer {

    private final InvoiceRepository invoiceRepository;
    private final PdfGeneratorService pdfGeneratorService;

    @KafkaListener(topics = "invoice-processing", groupId = "invoice-group")
    public void processInvoice(String invoiceId) {
        log.info("Received invoice from Kafka: {}", invoiceId);
        Invoice invoice = invoiceRepository.findById(invoiceId).orElse(null);
        if (invoice == null) return;

        invoice.setStatus("PROCESSING");
        invoiceRepository.save(invoice);
        log.info("Invoice {} is now PROCESSING", invoiceId);

        try {
            pdfGeneratorService.generateInvoicePdf(invoice);
        } catch (Exception e) {
            log.error("Failed to generate PDF for invoice {}", invoiceId, e);
            invoice.setStatus("FAILED");
            invoiceRepository.save(invoice);
            return;
        }


        invoice.setStatus("DONE");
        invoiceRepository.save(invoice);
        log.info("Invoice {} is DONE", invoiceId);
    }
}