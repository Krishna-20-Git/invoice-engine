package com.krishna.invoiceengine.kafka;

import com.krishna.invoiceengine.model.Invoice;
import com.krishna.invoiceengine.repository.InvoiceRepository;
import com.krishna.invoiceengine.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceConsumer {

    private final InvoiceRepository invoiceRepository;
    private final PdfGeneratorService pdfGeneratorService;

    @KafkaListener(topics = "invoice-processing", groupId = "invoice-group")
    public void processInvoice(String invoiceId) {
        log.info("Received invoice from Kafka: {}", invoiceId);

        try {
            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

            invoice.setStatus("PROCESSING");
            invoiceRepository.save(invoice);
            log.info("Invoice {} is now PROCESSING", invoiceId);

            // Generate PDF
            String pdfPath = pdfGeneratorService.generateInvoicePdf(invoice);
            log.info("PDF generated at: {}", pdfPath);

            invoice.setStatus("DONE");
            invoiceRepository.save(invoice);
            log.info("Invoice {} is DONE", invoiceId);

        } catch (Exception e) {
            log.error("Error processing invoice {}: {}", invoiceId, e.getMessage());
        }
    }
}