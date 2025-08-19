package br.com.controle_despesas.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.controle_despesas.model.Relatorio;

public class RelatorioExcelExporter {

    public static ByteArrayInputStream exportarParaExcel(List<Relatorio> relatorios, BigDecimal saldoFinalBanco) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Relatório");
            DataFormat format = workbook.createDataFormat();

            // ===== ESTILOS =====
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle positivoStyle = createValorStyle(workbook, format, IndexedColors.GREEN);
            CellStyle negativoStyle = createValorStyle(workbook, format, IndexedColors.RED);
            CellStyle totalLabelStyle = createTotalLabelStyle(workbook);
            CellStyle totalValorStyle = createTotalValorStyle(workbook, format);
            CellStyle totalMergedStyle = createMergedStyle(workbook);

            // ===== CABEÇALHO =====
            sheet.createFreezePane(0, 1);
            Row header = sheet.createRow(0);
            String[] colunas = { "Data", "Categoria", "Valor", "Descrição", "Pagamento", "Tipo" };
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(headerStyle);
            }

            // ===== DADOS =====
            int linha = 1;
            for (Relatorio r : relatorios) {
                Row row = sheet.createRow(linha++);

                row.createCell(0).setCellValue(r.getData().toString());
                row.getCell(0).setCellStyle(dataStyle);

                row.createCell(1).setCellValue(r.getNomeCategoria() != null ? r.getNomeCategoria() : r.getTipo());
                row.getCell(1).setCellStyle(dataStyle);

                BigDecimal valor = r.getTipo().equalsIgnoreCase("despesa") ? r.getValor().negate() : r.getValor();
                row.createCell(2).setCellValue(valor.doubleValue());
                row.getCell(2).setCellStyle(valor.doubleValue() >= 0 ? positivoStyle : negativoStyle);

                row.createCell(3).setCellValue(r.getDescricao() != null ? r.getDescricao() : "");
                row.getCell(3).setCellStyle(dataStyle);

                row.createCell(4).setCellValue(r.getPagamento() != null ? r.getPagamento() : "");
                row.getCell(4).setCellStyle(dataStyle);

                row.createCell(5).setCellValue(r.getTipo());
                row.getCell(5).setCellStyle(dataStyle);
            }

            // ===== TOTAIS =====
            int linhaTotal = linha + 1;

            criarLinhaTotal(sheet, linhaTotal++, "Total Receitas:",
                    "SUMIF(F2:F" + linha + ",\"receita\",C2:C" + linha + ")",
                    positivoStyle, totalLabelStyle, totalMergedStyle);

            criarLinhaTotal(sheet, linhaTotal++, "Total Despesas:",
                    "SUMIF(F2:F" + linha + ",\"despesa\",C2:C" + linha + ")",
                    negativoStyle, totalLabelStyle, totalMergedStyle);

            criarLinhaTotal(sheet, linhaTotal++, "Resultado (Receitas - Despesas):",
                    "C" + (linhaTotal - 2) + "-C" + (linhaTotal - 1),
                    totalValorStyle, totalLabelStyle, totalMergedStyle);

            // ===== SALDO FINAL BANCO =====
            int linhaSaldo = linhaTotal + 1;
            sheet.addMergedRegion(new CellRangeAddress(linhaSaldo, linhaSaldo, 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(linhaSaldo, linhaSaldo, 2, 5));

            Row saldoRow = sheet.createRow(linhaSaldo);
            Cell cellSaldoLabel = saldoRow.createCell(0);
            cellSaldoLabel.setCellValue("Saldo Final (Banco):");
            cellSaldoLabel.setCellStyle(totalLabelStyle);
            saldoRow.createCell(1).setCellStyle(totalMergedStyle);

            Cell cellSaldoValue = saldoRow.createCell(2);
            cellSaldoValue.setCellValue(saldoFinalBanco.doubleValue());
            cellSaldoValue.setCellStyle(saldoFinalBanco.doubleValue() >= 0 ? positivoStyle : negativoStyle);

            saldoRow.createCell(3).setCellStyle(totalMergedStyle);
            saldoRow.createCell(4).setCellStyle(totalMergedStyle);
            saldoRow.createCell(5).setCellStyle(totalMergedStyle);

            // Ajustar largura das colunas
            for (int i = 0; i < colunas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // ===== EXPORTAÇÃO =====
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar o arquivo Excel: " + e.getMessage(), e);
        }
    }

    // ===== HELPERS =====
    private static CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setAllBorders(style, BorderStyle.THIN);
        return style;
    }

    private static CellStyle createDataStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        setAllBorders(style, BorderStyle.THIN);
        return style;
    }

    private static CellStyle createValorStyle(Workbook wb, DataFormat format, IndexedColors cor) {
        CellStyle style = wb.createCellStyle();
        setAllBorders(style, BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat(format.getFormat("R$ #,##0.00"));
        Font font = wb.createFont();
        font.setColor(cor.getIndex());
        style.setFont(font);
        return style;
    }

    private static CellStyle createTotalLabelStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        setAllBorders(style, BorderStyle.THIN);
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createTotalValorStyle(Workbook wb, DataFormat format) {
        CellStyle style = wb.createCellStyle();
        setAllBorders(style, BorderStyle.THIN);
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat(format.getFormat("R$ #,##0.00"));
        return style;
    }

    private static CellStyle createMergedStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        setAllBorders(style, BorderStyle.THIN);
        return style;
    }

    private static void criarLinhaTotal(Sheet sheet, int linha, String label, String formula,
            CellStyle valorStyle, CellStyle labelStyle, CellStyle mergedStyle) {
        sheet.addMergedRegion(new CellRangeAddress(linha, linha, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(linha, linha, 2, 5));

        Row row = sheet.createRow(linha);

        Cell cellLabel = row.createCell(0);
        cellLabel.setCellValue(label);
        cellLabel.setCellStyle(labelStyle);
        row.createCell(1).setCellStyle(mergedStyle);

        Cell cellValue = row.createCell(2);
        cellValue.setCellFormula(formula);
        cellValue.setCellStyle(valorStyle);

        row.createCell(3).setCellStyle(mergedStyle);
        row.createCell(4).setCellStyle(mergedStyle);
        row.createCell(5).setCellStyle(mergedStyle);
    }

    private static void setAllBorders(CellStyle style, BorderStyle border) {
        style.setBorderBottom(border);
        style.setBorderTop(border);
        style.setBorderLeft(border);
        style.setBorderRight(border);
    }
}
