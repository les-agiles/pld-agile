package fr.insa.geofast.services;

import com.itextpdf.io.exceptions.IOException;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import lombok.extern.slf4j.Slf4j;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Canvas;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Date;


@Slf4j
public class PdfGenerator {

    private static final String GEOFAST_LOGO = "src/main/resources/fr/insa/geofast/GeoFast-compressed.png";
    private static final String BACKGROUND = "src/main/resources/fr/insa/geofast/pdf-background.png";


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
        PageSize pageSize = new PageSize(PageSize.A4);
        document.setTopMargin(50);
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new TextFooterEventHandler(document));
        pdf.addEventHandler(PdfDocumentEvent.INSERT_PAGE, new BackgroundEventHandler(pageSize));

        pdf.addNewPage();
        Paragraph title = new Paragraph("Programme de livraison")
                .setFontColor(new DeviceRgb(0,0,0))
                .setFontSize(26f)
                .setBold();
        document.add(
                new Table(new float[]{pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin()})
                        .addCell(new Cell().add(title).setBorder(Border.NO_BORDER))
                        .setTextAlignment(TextAlignment.CENTER)
        );

        for (int i = 0; i < 5; i++)
        {
            addDeliveryGuyProgram(document, pageSize, i);
            if(i < 5-1)
            {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }
        }

        document.close();
    }

    private void addDeliveryGuyProgram(Document document, PageSize pageSize, int index)
    {
        float[] columnsWidth = {150f, pageSize.getWidth() - 150f};

        Table deliveryGuyHeader = new Table(columnsWidth);
        deliveryGuyHeader.addCell(new Cell().add(new Paragraph("Livreur "+index).setBold().setFontSize(18f)).setVerticalAlignment(VerticalAlignment.BOTTOM).setBorder(Border.NO_BORDER));
        deliveryGuyHeader.addCell(new Cell().add(new Paragraph("Départ du dépôt : 8h").setFontSize(14f)).setVerticalAlignment(VerticalAlignment.BOTTOM).setBorder(Border.NO_BORDER));

        document.add(deliveryGuyHeader);
        SolidLine solidLine = new SolidLine(1f);
        document.add(new LineSeparator(solidLine));

        for(int i = 0; i < 5; i++)
        {
            addDeliveryRequest(document, pageSize, i);
        }
    }

    private void addDeliveryRequest(Document document, PageSize pageSize, int index)
    {
        Table requestHeader = new Table(new float[]{50f, (pageSize.getWidth()-50f)/2, (pageSize.getWidth()-50f)/2});
        requestHeader.addCell(
            new Cell().add(
                new Paragraph(""+index)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setHeight(30f)
                    .setWidth(30f)
                    .setFontSize(20f)
                    .setBold()
                    .setBorder(new SolidBorder(2))
                .setBorderRadius(new BorderRadius(100))
            )
            .setTextAlignment(TextAlignment.CENTER)
            .setBorder(Border.NO_BORDER)
        );
        Table requestTimeInfo = new Table(new float[]{ (pageSize.getWidth()-50f)/2});

        requestTimeInfo.addCell(new Cell().add(new Paragraph("Arrivée prévue à 8:20")).setBorder(Border.NO_BORDER).setPadding(0));
        requestTimeInfo.addCell(new Cell().add(new Paragraph("Temps de livraison : 2min")).setBorder(Border.NO_BORDER).setPadding(0));

        requestHeader.addCell(new Cell().add(requestTimeInfo).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        requestHeader.addCell(new Cell().add(new Paragraph("lat. : 45.002011; lon. : 2.645644")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        document.add(requestHeader);

        addRoute(document, pageSize);
        SolidLine solidLine = new SolidLine(1f);
        document.add(new LineSeparator(solidLine));
    }

    private void addRoute(Document document, PageSize pageSize){

        Table route = new Table(new float[]{pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin()})
                .setHorizontalBorderSpacing(0)
                .setVerticalBorderSpacing(0);

        Paragraph routeLabel = new Paragraph("Itinéraire").setBold().setFontSize(14f).setMarginLeft(20f);
        route.addCell(new Cell().add(routeLabel).setBorder(Border.NO_BORDER).setPaddings(0,0,0,0));
        for(int i = 0; i < 5 ;i++){

            Paragraph step = new Paragraph("Emprunter rue "+i).setFontSize(12f).setMarginLeft(40f);
            Paragraph next = new Paragraph("puis").setFontSize(10f).setMarginLeft(40f).setFontColor(ColorConstants.GRAY);

            route.addCell(new Cell().add(step).setBorder(Border.NO_BORDER).setPadding(0));
            if (i < 5-1) {
                route.addCell(new Cell().add(next).setBorder(Border.NO_BORDER).setPadding(0));
            }

        }
        Paragraph arrivalLabel = new Paragraph("Arrivée").setBold().setFontSize(14f).setMarginLeft(20f);
        route.addCell(new Cell().add(arrivalLabel).setBorder(Border.NO_BORDER).setPadding(0));

        document.add(route);
    }

    private static class BackgroundEventHandler implements IEventHandler {

        protected PageSize pageSize;

        public BackgroundEventHandler(PageSize pageSize) {
            this.pageSize = pageSize;
        }

        @Override
        public void handleEvent(Event event) {
            try {
                PdfDocumentEvent pdfDocumentEvent = (PdfDocumentEvent)event;

                PdfCanvas canvas = new PdfCanvas(pdfDocumentEvent.getPage());
                canvas.addImageFittedIntoRectangle(ImageDataFactory.create(BACKGROUND), pageSize, false);
            } catch (MalformedURLException e) {
                log.error(e.getMessage());
            }
        }
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
                geoFastLogo = new Image(ImageDataFactory.create(GEOFAST_LOGO));
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
