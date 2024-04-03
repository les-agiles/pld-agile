package fr.insa.geofast.services;

import com.itextpdf.io.exceptions.IOException;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.File;


@Slf4j
public class PdfGenerator {

    private PdfGenerator() {

    }

    public static void generatePdf() throws IOException {
        log.info("Generating PDF...");
        try {
            File file = new File("./hello-pdf.pdf");
            file.getParentFile().mkdirs();

            new PdfGenerator().manipulatePdf("./hello-pdf.pdf");
            log.info("PDF generation ended.");
        } catch (IOException | FileNotFoundException e) {
            log.error(e.getMessage());
        }
    }

    public void manipulatePdf(String dest) throws IOException, FileNotFoundException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf);

        List topLevel = new List();
        ListItem topLevelItem = new ListItem();
        topLevelItem.add(new Paragraph().add("Item 1"));
        topLevel.add(topLevelItem);

        List secondLevel = new List();
        secondLevel.add("Sub Item 1");
        ListItem secondLevelItem = new ListItem();
        secondLevelItem.add(new Paragraph("Sub Item 2"));
        secondLevel.add(secondLevelItem);
        topLevelItem.add(secondLevel);

        List thirdLevel = new List();
        thirdLevel.add("Sub Sub Item 1");
        thirdLevel.add("Sub Sub Item 2");
        secondLevelItem.add(thirdLevel);

        document.add(topLevel);

        document.close();
    }


}
