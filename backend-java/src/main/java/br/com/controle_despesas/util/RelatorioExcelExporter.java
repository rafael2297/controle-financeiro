package br.com.controle_despesas.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.controle_despesas.model.Relatorio;

public class RelatorioExcelExporter {

    public static ByteArrayInputStream exportarParaExcel(List<Relatorio> relatorios) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Relatório");

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);

            CellStyle valorPositivo = workbook.createCellStyle();
            valorPositivo.cloneStyleFrom(dataStyle);
            valorPositivo.setAlignment(HorizontalAlignment.RIGHT);
            Font verde = workbook.createFont();
            verde.setColor(IndexedColors.GREEN.getIndex());
            valorPositivo.setFont(verde);

            CellStyle valorNegativo = workbook.createCellStyle();
            valorNegativo.cloneStyleFrom(dataStyle);
            valorNegativo.setAlignment(HorizontalAlignment.RIGHT);
            Font vermelho = workbook.createFont();
            vermelho.setColor(IndexedColors.RED.getIndex());
            valorNegativo.setFont(vermelho);

            // Cabeçalho
            Row header = sheet.createRow(0);
            String[] colunas = { "Data", "Tipo", "Valor", "Descrição" };
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Preencher dados
            int linha = 1;
            for (Relatorio r : relatorios) {
                Row row = sheet.createRow(linha++);

                row.createCell(0).setCellValue(r.getData().toString());
                row.createCell(1).setCellValue(r.getTipo());

                Cell valorCell = row.createCell(2);
                BigDecimal valor = r.getValor();
                valorCell.setCellValue(valor.doubleValue());
                valorCell.setCellStyle(valor.doubleValue() >= 0 ? valorPositivo : valorNegativo);

                row.createCell(3).setCellValue(r.getDescricao() != null ? r.getDescricao() : "");
            }

            // Total
            int linhaTotal = linha;
            sheet.addMergedRegion(new CellRangeAddress(linhaTotal, linhaTotal, 0, 2));

            Row totalRow = sheet.createRow(linhaTotal);
            totalRow.createCell(0).setCellValue("Total:");

            Cell totalCell = totalRow.createCell(2);
            totalCell.setCellFormula("SUM(C2:C" + linhaTotal + ")");

            // Ajusta largura colunas
            for (int i = 0; i < colunas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel: " + e.getMessage(), e);
        }
    }
}
