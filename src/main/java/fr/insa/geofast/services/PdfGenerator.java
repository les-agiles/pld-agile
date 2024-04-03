package fr.insa.geofast.services;

import com.itextpdf.io.exceptions.IOException;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import lombok.extern.slf4j.Slf4j;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Date;


@Slf4j
public class PdfGenerator {

    private PdfGenerator() {

    }

    public static void generatePdf() throws IOException {
        log.info("Generating PDF...");
        String fileName = getNewFileName();
        try {
            new PdfGenerator().manipulatePdf(fileName);
            log.info("PDF generation ended.");
        } catch (IOException | FileNotFoundException e) {
            log.error(e.getMessage());
        }
    }

    private static String getNewFileName() {
        return "planning-request-" + new Date().getTime() + ".pdf";
    }

    protected static String getFormattedDate() {
        return new Date().toString();
    }

    public void manipulatePdf(String dest) throws IOException, FileNotFoundException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf);
        document.setTopMargin(50);
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new TextFooterEventHandler(document));


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


    private static class TextFooterEventHandler implements IEventHandler {
        protected Document doc;

        public TextFooterEventHandler(Document doc) {
            this.doc = doc;
        }

        @Override
        public void handleEvent(Event currentEvent) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) currentEvent;
            Rectangle pageSize = docEvent.getPage().getPageSize();

            float leftX = pageSize.getLeft() + doc.getLeftMargin();
            float rightX = ((pageSize.getLeft() + doc.getLeftMargin())
                    + (pageSize.getRight() - doc.getRightMargin())) - 100;
            float centerX = (pageSize.getLeft() + doc.getLeftMargin() + pageSize.getRight() - doc.getRightMargin()) / 2;
            float headerY = pageSize.getTop() - doc.getTopMargin() + doc.getTopMargin() / 2;
            float footerY = doc.getBottomMargin();

            Image geoFastLogo = null;
            try {
                geoFastLogo = new Image(ImageDataFactory.create("src/main/resources/fr/insa/geofast/GeoFast-compressed.png"));
                geoFastLogo.setFixedPosition(leftX + 20, headerY - 10);
                geoFastLogo.setHeight(25);
            } catch (MalformedURLException e) {
                log.error(e.getMessage());
            }

            Canvas canvas = new Canvas(docEvent.getPage(), pageSize);
            canvas
                    .setFontSize(8)
                    .add(geoFastLogo)
                    .showTextAligned("Exporté le " + getFormattedDate(), rightX, headerY, TextAlignment.CENTER)
                    .showTextAligned("GeoFast", centerX, footerY, TextAlignment.CENTER)
                    .close();
        }
    }


}
