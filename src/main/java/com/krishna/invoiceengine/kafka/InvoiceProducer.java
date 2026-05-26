package com.krishna.invoiceengine.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "invoice-processing";

    public void sendInvoice(String invoiceId) {
        kafkaTemplate.send(TOPIC, invoiceId);
        log.info("Sent invoice to Kafka topic: {}", invoiceId);
    }
}