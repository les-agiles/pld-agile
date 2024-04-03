package fr.insa.geofast.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;

@Slf4j
public class PdfGenerator {

    private PdfGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static void generatePdf() {
        log.info("Generating PDF...");
        try (Document document = new Document(new PdfDocument(new PdfWriter("./hello-pdf.pdf")))) {
            document.add(new Paragraph("Hello PDF!"));
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }
    }
}
