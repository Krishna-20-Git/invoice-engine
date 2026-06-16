package com.krishna.invoiceengine.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.krishna.invoiceengine.model.Invoice;
import com.krishna.invoiceengine.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfGeneratorService {

    private final InvoiceRepository invoiceRepository;
    private static final String PDF_DIR = "invoices/";

    @Async
    public void processInvoiceAsync(Invoice invoice) {
        try {
            invoice.setStatus("PROCESSING");
            invoiceRepository.save(invoice);

            generateInvoicePdf(invoice);

            invoice.setStatus("DONE");
            invoiceRepository.save(invoice);
            log.info("Invoice {} processed successfully", invoice.getId());

        } catch (Exception e) {
            log.error("Failed to process invoice {}", invoice.getId(), e);
            invoice.setStatus("FAILED");
            invoiceRepository.save(invoice);
        }
    }

    public String generateInvoicePdf(Invoice invoice) throws IOException {
        new File(PDF_DIR).mkdirs();
        String filePath = PDF_DIR + "invoice-" + invoice.getId() + ".pdf";
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("INVOICE").setBold().setFontSize(24));
        document.add(new Paragraph(" "));
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2})).useAllAvailableWidth();
        table.addCell("Invoice ID");
        table.addCell(invoice.getId());
        table.addCell("Company");
        table.addCell(invoice.getCompanyName());
        table.addCell("Amount");
        table.addCell("$" + invoice.getAmount());
        table.addCell("Status");
        table.addCell(invoice.getStatus());
        table.addCell("Date");
        table.addCell(invoice.getCreatedAt().toString());
        document.add(table);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Thank you for your business!").setItalic());
        document.close();
        log.info("PDF generated: {}", filePath);
        return filePath;
    }
}