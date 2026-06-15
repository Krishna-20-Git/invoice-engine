package com.krishna.invoiceengine.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class InvoiceProducer {

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendInvoice(UUID invoiceId) {
        if (kafkaTemplate != null) {
            kafkaTemplate.send("invoice-processing", invoiceId.toString());
            log.info("Sent invoice to Kafka: {}", invoiceId);
        } else {
            log.info("Kafka not available - skipping message for invoice: {}", invoiceId);
        }
    }
}