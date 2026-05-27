package com.krishna.invoiceengine.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.krishna.invoiceengine.model.Invoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class PdfGeneratorService {

    private static final String PDF_DIR = "invoices/";

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
