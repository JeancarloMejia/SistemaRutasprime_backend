package com.backend.avance1.service;

import com.backend.avance1.entity.Viaje;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfService {

    @Value("${empresa.nombre}")
    private String empresaNombre;

    @Value("${empresa.ruc}")
    private String empresaRuc;

    @Value("${empresa.direccion}")
    private String empresaDireccion;

    @Value("${empresa.telefono}")
    private String empresaTelefono;

    @Value("${empresa.email}")
    private String empresaEmail;

    public File generarBoletaViaje(Viaje viaje) throws IOException {
        String fileName = "boleta_viaje_" + viaje.getId() + ".pdf";
        String filePath = System.getProperty("java.io.tmpdir") + File.separator + fileName;

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        DeviceRgb colorPrimario = new DeviceRgb(102, 126, 234);
        DeviceRgb colorSecundario = new DeviceRgb(118, 75, 162);

        Table headerTable = new Table(2);
        headerTable.setWidth(UnitValue.createPercentValue(100));
        headerTable.setMarginBottom(20);

        Cell empresaCell = new Cell()
                .add(new Paragraph(empresaNombre).setBold().setFontSize(14).setFontColor(colorPrimario))
                .add(new Paragraph("RUC: " + empresaRuc).setFontSize(9))
                .add(new Paragraph(empresaDireccion).setFontSize(9))
                .add(new Paragraph("Tel: " + empresaTelefono).setFontSize(9))
                .add(new Paragraph(empresaEmail).setFontSize(9))
                .setBorder(null)
                .setPadding(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaEmision = viaje.getCreatedAt() != null
                ? viaje.getCreatedAt().format(formatter)
                : LocalDateTime.now().format(formatter);

        Cell fechaCell = new Cell()
                .add(new Paragraph("N° " + String.format("%06d", viaje.getId())).setBold().setFontSize(12))
                .add(new Paragraph(fechaEmision).setFontSize(9))
                .setBorder(null)
                .setPadding(0)
                .setTextAlignment(TextAlignment.RIGHT);

        headerTable.addCell(empresaCell);
        headerTable.addCell(fechaCell);
        document.add(headerTable);

        Paragraph titulo = new Paragraph("BOLETA DE VIAJE")
                .setFontSize(24)
                .setBold()
                .setFontColor(colorPrimario)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("Comprobante de Servicio de Transporte")
                .setFontSize(12)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitulo);

        Table infoGeneralTable = new Table(2);
        infoGeneralTable.setWidth(UnitValue.createPercentValue(100));

        addHeaderCell(infoGeneralTable, "INFORMACIÓN GENERAL", colorPrimario, 2);
        addInfoRow(infoGeneralTable, "N° de Viaje:", String.format("%06d", viaje.getId()));
        addInfoRow(infoGeneralTable, "Fecha de Emisión:", fechaEmision);
        addInfoRow(infoGeneralTable, "Estado:", viaje.getEstado().toString());

        if (viaje.getChargeId() != null) {
            addInfoRow(infoGeneralTable, "ID de Pago:", viaje.getChargeId());
        }

        document.add(infoGeneralTable);
        document.add(new Paragraph("\n"));

        Table clienteTable = new Table(2);
        clienteTable.setWidth(UnitValue.createPercentValue(100));

        addHeaderCell(clienteTable, "DATOS DEL CLIENTE", colorPrimario, 2);
        addInfoRow(clienteTable, "Nombre:", viaje.getNombre() + " " + viaje.getApellido());
        addInfoRow(clienteTable, "Email:", viaje.getEmailCliente());

        document.add(clienteTable);
        document.add(new Paragraph("\n"));

        if (viaje.getConductorId() != null) {
            Table conductorTable = new Table(2);
            conductorTable.setWidth(UnitValue.createPercentValue(100));

            addHeaderCell(conductorTable, "DATOS DEL CONDUCTOR", colorSecundario, 2);
            addInfoRow(conductorTable, "Nombre:", viaje.getConductorNombres() + " " + viaje.getConductorApellidos());
            addInfoRow(conductorTable, "Celular:", viaje.getConductorCelular());
            addInfoRow(conductorTable, "Email:", viaje.getConductorEmail());

            document.add(conductorTable);
            document.add(new Paragraph("\n"));

            Table vehiculoTable = new Table(2);
            vehiculoTable.setWidth(UnitValue.createPercentValue(100));

            addHeaderCell(vehiculoTable, "DATOS DEL VEHÍCULO", colorSecundario, 2);
            addInfoRow(vehiculoTable, "Marca:", viaje.getVehiculoMarca());
            addInfoRow(vehiculoTable, "Color:", viaje.getVehiculoColor());
            addInfoRow(vehiculoTable, "Placa:", viaje.getVehiculoPlaca());
            addInfoRow(vehiculoTable, "Año:", String.valueOf(viaje.getVehiculoAnio()));

            document.add(vehiculoTable);
            document.add(new Paragraph("\n"));
        }

        Table viajeTable = new Table(2);
        viajeTable.setWidth(UnitValue.createPercentValue(100));

        addHeaderCell(viajeTable, "DETALLES DEL VIAJE", colorPrimario, 2);
        addInfoRow(viajeTable, "Tipo de Servicio:", viaje.getTipo());
        addInfoRow(viajeTable, "Origen:", viaje.getOrigen());
        addInfoRow(viajeTable, "Destino:", viaje.getDestino());
        addInfoRow(viajeTable, "Distancia:", String.format("%.2f km", viaje.getDistanciaKm()));

        if (viaje.getComentarios() != null && !viaje.getComentarios().isEmpty()) {
            addInfoRow(viajeTable, "Comentarios:", viaje.getComentarios());
        }

        document.add(viajeTable);
        document.add(new Paragraph("\n"));

        Table totalTable = new Table(2);
        totalTable.setWidth(UnitValue.createPercentValue(100));

        Cell totalLabelCell = new Cell()
                .add(new Paragraph("TOTAL PAGADO").setBold().setFontSize(14))
                .setBackgroundColor(new DeviceRgb(245, 245, 245))
                .setPadding(10)
                .setTextAlignment(TextAlignment.RIGHT);

        Cell totalValueCell = new Cell()
                .add(new Paragraph(String.format("S/ %.2f", viaje.getPrecio())).setBold().setFontSize(16).setFontColor(colorPrimario))
                .setBackgroundColor(new DeviceRgb(245, 245, 245))
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER);

        totalTable.addCell(totalLabelCell);
        totalTable.addCell(totalValueCell);
        document.add(totalTable);

        Paragraph footer = new Paragraph("\nGracias por confiar en " + empresaNombre)
                .setFontSize(10)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30);
        document.add(footer);

        Paragraph footerContacto = new Paragraph("Para consultas: " + empresaTelefono + " | " + empresaEmail)
                .setFontSize(8)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(footerContacto);

        document.close();

        log.info("PDF generado exitosamente: {}", filePath);
        return new File(filePath);
    }

    private void addHeaderCell(Table table, String text, DeviceRgb color, int colspan) {
        Cell cell = new Cell(1, colspan)
                .add(new Paragraph(text).setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(color)
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(cell);
    }

    private void addInfoRow(Table table, String label, String value) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setBold())
                .setPadding(8)
                .setBackgroundColor(new DeviceRgb(250, 250, 250));

        Cell valueCell = new Cell()
                .add(new Paragraph(value))
                .setPadding(8);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}