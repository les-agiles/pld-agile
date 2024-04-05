package fr.insa.geofast.services;

import com.graphhopper.ResponsePath;
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
import fr.insa.geofast.exceptions.IHMException;
import fr.insa.geofast.models.DeliveryGuy;
import lombok.extern.slf4j.Slf4j;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Canvas;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.*;

@Slf4j
public class PdfGenerator {

    private static final String GEOFAST_LOGO = "src/main/resources/fr/insa/geofast/GeoFast-compressed.png";
    private static final String BACKGROUND = "src/main/resources/fr/insa/geofast/pdf-background.png";

    private static final String STR_LAT_LONG_FORMAT = "lat. : %f; lon. : %f";

    private final Map<String, DeliveryGuy> deliveryGuysMap;

    private PdfGenerator(Map<String, DeliveryGuy> deliveryGuyMap) {
        this.deliveryGuysMap = deliveryGuyMap;
    }

    public static void generatePdf(Map<String, DeliveryGuy> deliveryGuyMap, String path) throws IHMException {
        log.info("Generating PDF...");
        try {
            new PdfGenerator(deliveryGuyMap).manipulatePdf(path);
            log.info("PDF generation ended.");
        } catch (IOException | FileNotFoundException e) {
            log.error(e.getMessage());
            throw new IHMException("Le fichier PDF n'a pas pu être généré.");
        }
    }

    protected static String getFormattedDate() {
        return new Date().toString();
    }

    private void manipulatePdf(String dest) throws IOException, FileNotFoundException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf);
        PageSize pageSize = new PageSize(PageSize.A4);
        document.setTopMargin(50);
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new TextFooterEventHandler(document));
        pdf.addEventHandler(PdfDocumentEvent.INSERT_PAGE, new BackgroundEventHandler(pageSize));

        pdf.addNewPage();
        Paragraph title = new Paragraph("Programme de livraison")
                .setFontColor(new DeviceRgb(0, 0, 0))
                .setFontSize(26f)
                .setBold();
        document.add(
                new Table(new float[]{pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin()})
                        .addCell(new Cell().add(title).setBorder(Border.NO_BORDER))
                        .setTextAlignment(TextAlignment.CENTER)
        );

        for (DeliveryGuy deliveryGuy : deliveryGuysMap.values()) {
            addDeliveryGuyProgram(document, pageSize, deliveryGuy);
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        }

        pdf.removePage(pdf.getLastPage());
        document.close();
    }

    private void addDeliveryGuyProgram(Document document, PageSize pageSize, DeliveryGuy deliveryGuy) {
        float[] columnsWidth = {150f, 200f, pageSize.getWidth() - 350f};

        Table deliveryGuyHeader = new Table(columnsWidth);
        deliveryGuyHeader.addCell(new Cell().add(new Paragraph(String.format("Livreur %s", deliveryGuy.getId())).setBold().setFontSize(18f)).setVerticalAlignment(VerticalAlignment.BOTTOM).setBorder(Border.NO_BORDER));
        deliveryGuyHeader.addCell(new Cell().add(new Paragraph("Départ du dépôt : 8h").setFontSize(14f)).setVerticalAlignment(VerticalAlignment.BOTTOM).setBorder(Border.NO_BORDER));
        deliveryGuyHeader.addCell(
                new Cell().add(
                                new Paragraph(String.format(STR_LAT_LONG_FORMAT, deliveryGuy.getRoute().getWarehouse().getAddress().getLatitude(), deliveryGuy.getRoute().getWarehouse().getAddress().getLongitude()))
                                        .setFontSize(14f)
                        )
                        .setVerticalAlignment(VerticalAlignment.BOTTOM)
                        .setBorder(Border.NO_BORDER)
        );

        document.add(deliveryGuyHeader);
        SolidLine solidLine = new SolidLine(1f);
        document.add(new LineSeparator(solidLine));

        for (int i = 0; i < deliveryGuy.getRoute().getRequestsOrdered().size(); i++) {
            addDeliveryRequest(document, pageSize, deliveryGuy, i);
        }

        addBackToWarehouse(document, pageSize, deliveryGuy);
    }

    private void addBackToWarehouse(Document document, PageSize pageSize, DeliveryGuy deliveryGuy) {
        addDeliveryRequestHeader(document, pageSize, deliveryGuy);
        addRoute(document, pageSize, deliveryGuy.getRoute().getBestRoute().get(deliveryGuy.getRoute().getBestRoute().size() - 1));
        SolidLine solidLine = new SolidLine(1f);
        document.add(new LineSeparator(solidLine));
    }

    private void addDeliveryRequest(Document document, PageSize pageSize, DeliveryGuy deliveryGuy, int index) {
        addDeliveryRequestHeader(document, pageSize, deliveryGuy, index);

        addRoute(document, pageSize, deliveryGuy.getRoute().getBestRoute().get(index));

        SolidLine solidLine = new SolidLine(1f);
        document.add(new LineSeparator(solidLine));
    }

    private void addDeliveryRequestHeader(Document document, PageSize pageSize, DeliveryGuy deliveryGuy, int index) {

        Table requestHeader = new Table(new float[]{50f, (pageSize.getWidth() - 50f) / 2, (pageSize.getWidth() - 50f) / 2});
        requestHeader.addCell(
                new Cell().add(
                                new Paragraph("" + (index + 1))
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
        Table requestTimeInfo = new Table(new float[]{(pageSize.getWidth() - 50f) / 2});

        int arrivalHour = deliveryGuy.getRoute().getRequestsOrdered().get(index).getArrivalDate().getHour();
        int arrivalMinutes = deliveryGuy.getRoute().getRequestsOrdered().get(index).getArrivalDate().getMinute();
        requestTimeInfo.addCell(new Cell().add(new Paragraph(String.format("Arrivée prévue à %02d:%02d", arrivalHour, arrivalMinutes))).setBorder(Border.NO_BORDER).setPadding(0));

        if(deliveryGuy.getRoute().getRequestsOrdered().get(index).getDeliveryDuration() > 0)
        {
            int durationMinutes = deliveryGuy.getRoute().getRequestsOrdered().get(index).getDeliveryDuration() / 60;
            int durationSeconds = deliveryGuy.getRoute().getRequestsOrdered().get(index).getDeliveryDuration() % 60;

            requestTimeInfo.addCell(new Cell().add(new Paragraph(String.format("Temps de livraison : %02dmin %02dsec", durationMinutes, durationSeconds))).setBorder(Border.NO_BORDER).setPadding(0));
        }

        requestHeader.addCell(new Cell().add(requestTimeInfo).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        double lat = deliveryGuy.getRoute().getRequestsOrdered().get(index).getDeliveryAddress().getLatitude();
        double lon = deliveryGuy.getRoute().getRequestsOrdered().get(index).getDeliveryAddress().getLongitude();
        requestHeader.addCell(new Cell().add(new Paragraph(String.format(STR_LAT_LONG_FORMAT, lat, lon))).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        document.add(requestHeader);
    }

    private void addDeliveryRequestHeader(Document document, PageSize pageSize, DeliveryGuy deliveryGuy) {

        Table requestHeader = new Table(new float[]{150f, (pageSize.getWidth() - 150f)});
        requestHeader.addCell(
                new Cell().add(
                                new Paragraph("Retour dépôt")
                                        .setFontSize(14f)
                                        .setBold()
                                        .setItalic()
                        )
                        .setBorder(Border.NO_BORDER)
        );

        double lat = deliveryGuy.getRoute().getWarehouse().getAddress().getLatitude();
        double lon = deliveryGuy.getRoute().getWarehouse().getAddress().getLongitude();

        requestHeader.addCell(new Cell().add(new Paragraph(String.format(STR_LAT_LONG_FORMAT, lat, lon))).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        document.add(requestHeader);
    }

    private void addRoute(Document document, PageSize pageSize, ResponsePath responsePath) {

        Table route = new Table(new float[]{pageSize.getWidth() - document.getLeftMargin() - document.getRightMargin()})
                .setHorizontalBorderSpacing(0)
                .setVerticalBorderSpacing(0);

        Paragraph routeLabel = new Paragraph("Itinéraire").setBold().setFontSize(14f).setMarginLeft(20f);
        route.addCell(new Cell().add(routeLabel).setBorder(Border.NO_BORDER).setPaddings(0, 0, 0, 0));

        for (int i = 0; i < responsePath.getInstructions().size(); i++) {
            String stepStr = translateInstruction(responsePath.getInstructions().get(i).getSign());
            if (!responsePath.getInstructions().get(i).getName().isEmpty()) {
                stepStr += " sur " + responsePath.getInstructions().get(i).getName();
            }
            Paragraph step = new Paragraph(stepStr).setFontSize(12f).setMarginLeft(40f);
            Paragraph next = new Paragraph(String.format("puis dans %dm", (int) responsePath.getInstructions().get(i).getDistance())).setFontSize(10f).setMarginLeft(40f).setFontColor(ColorConstants.GRAY);

            route.addCell(new Cell().add(step).setBorder(Border.NO_BORDER).setPadding(0));
            if (i < responsePath.getInstructions().size() - 1) {
                route.addCell(new Cell().add(next).setBorder(Border.NO_BORDER).setPadding(0));
            }
        }
        document.add(route);
    }

    private String translateInstruction(int sign) {

        return switch (sign) {
            case -98 -> "Faire un demi-tour";
            case -8 -> "Faire un demi-tour à gauche";
            case -7 -> "Rester à gauche";
            case -6 -> "Sortir du rond point";
            case -3 -> "Bifurquer à gauche";
            case -2 -> "Tourner à gauche";
            case -1 -> "Tourner légèrement à gauche";
            case 0 -> "Continuer";
            case 1 -> "Tourner légèrement à droite";
            case 2 -> "Tourner à droite";
            case 3 -> "Bifurquer à droite";
            case 4 -> "Arrivée à destination";
            case 5 -> "VIA WTF";
            case 6 -> "Prendre le rond-point";
            case 7 -> "Rester à droite";
            case 8 -> "Faire un demi-tour à droite";
            case 101 -> "START";
            case 102 -> "TRANSFER";
            case 103 -> "END";
            default -> "Emprunter";
        };
    }

    private static class BackgroundEventHandler implements IEventHandler {

        protected PageSize pageSize;

        public BackgroundEventHandler(PageSize pageSize) {
            this.pageSize = pageSize;
        }

        @Override
        public void handleEvent(Event event) {
            try {
                PdfDocumentEvent pdfDocumentEvent = (PdfDocumentEvent) event;

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

            try( Canvas canvas = new Canvas(docEvent.getPage(), pageSize)){
                canvas
                        .setFontSize(8)
                        .add(geoFastLogo)
                        .showTextAligned("Exporté le " + getFormattedDate(), rightX, headerY, TextAlignment.CENTER)
                        .showTextAligned("GeoFast", centerX, footerY, TextAlignment.CENTER)
                        .close();
            }catch (Exception e){
                log.error(e.getMessage());
            }
        }
    }
}