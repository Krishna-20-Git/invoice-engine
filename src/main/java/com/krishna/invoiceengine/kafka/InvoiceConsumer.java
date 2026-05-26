package com.krishna.invoiceengine.kafka;

import com.krishna.invoiceengine.model.Invoice;
import com.krishna.invoiceengine.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceConsumer {

    private final InvoiceRepository invoiceRepository;

    @KafkaListener(topics = "invoice-processing", groupId = "invoice-group")
    public void processInvoice(String invoiceId) {
        log.info("Received invoice from Kafka: {}", invoiceId);

        try {
            // Step 1: Fetch invoice from database
            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

            // Step 2: Update status to PROCESSING
            invoice.setStatus("PROCESSING");
            invoiceRepository.save(invoice);
            log.info("Invoice {} is now PROCESSING", invoiceId);

            // Step 3: Simulate processing work (PDF generation will go here later)
            Thread.sleep(2000);

            // Step 4: Update status to DONE
            invoice.setStatus("DONE");
            invoiceRepository.save(invoice);
            log.info("Invoice {} is DONE", invoiceId);

        } catch (Exception e) {
            log.error("Error processing invoice {}: {}", invoiceId, e.getMessage());
        }
    }
}