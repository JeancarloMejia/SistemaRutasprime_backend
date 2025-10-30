package com.backend.avance1.service;

import com.backend.avance1.entity.User;
import com.backend.avance1.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserExcelService implements UserExcelServiceInterface {

    private final UserRepository userRepository;

    public UserExcelService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public byte[] exportUsersToExcel() throws IOException {
        List<User> users = userRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Usuarios");

        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String titulo = "LISTA DE USUARIOS DE RUTAS PRIME HASTA LA FECHA (" + fecha + ")";

        Row titleRow = sheet.createRow(1);
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue(titulo);

        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setWrapText(true);
        titleCell.setCellStyle(titleStyle);

        sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 9));

        // Aumentar altura del título
        titleRow.setHeightInPoints(40);

        try (InputStream logoStream = new ClassPathResource("static/logo.jpg").getInputStream()) {
            byte[] bytes = logoStream.readAllBytes();
            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
            CreationHelper helper = workbook.getCreationHelper();
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(0);
            anchor.setRow1(1);
            anchor.setCol2(1);
            anchor.setRow2(3);
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            pict.resize(1.2); // Ajusta tamaño
        } catch (Exception e) {
            System.out.println("No se pudo cargar el logo: " + e.getMessage());
        }

        Row header = sheet.createRow(4);
        String[] columns = {
                "ID", "Nombres", "Apellidos", "Celular",
                "Email", "Dirección", "DNI/RUC", "Activo", "Roles"
        };

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i + 1);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 5;
        for (User u : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(1).setCellValue(u.getId());
            row.createCell(2).setCellValue(u.getNombres());
            row.createCell(3).setCellValue(u.getApellidos());
            row.createCell(4).setCellValue(u.getCelular());
            row.createCell(5).setCellValue(u.getEmail());
            row.createCell(6).setCellValue(u.getDireccion());
            row.createCell(7).setCellValue(u.getDniRuc());
            row.createCell(8).setCellValue(u.isActivo() ? "Sí" : "No");
            row.createCell(9).setCellValue(String.join(", ", u.getRoles().stream().map(Enum::name).toList()));
        }

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        for (int i = 5; i < rowNum; i++) {
            Row row = sheet.getRow(i);
            for (int j = 1; j <= 9; j++) {
                Cell cell = row.getCell(j);
                if (cell != null) {
                    cell.setCellStyle(cellStyle);
                }
            }
        }

        Row footer = sheet.createRow(rowNum + 2);
        Cell footerCell = footer.createCell(1);
        footerCell.setCellValue("Generado automáticamente por RUTAS PRIME © 2025");

        CellStyle footerStyle = workbook.createCellStyle();
        Font footerFont = workbook.createFont();
        footerFont.setItalic(true);
        footerFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        footerStyle.setFont(footerFont);
        footerStyle.setAlignment(HorizontalAlignment.CENTER);
        footerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        footerCell.setCellStyle(footerStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum + 2, rowNum + 2, 1, 9));

        for (int i = 1; i <= 9; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, Math.min(currentWidth + 800, 256 * 40));
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }
}