package br.com.controle_despesas.util;

import br.com.controle_despesas.model.Relatorio;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class RelatorioExcelExporter {

    public static ByteArrayInputStream exportarParaExcel(List<Relatorio> relatorios) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Relatório");

            // ===== ESTILOS =====
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);

            // Estilo para valores numéricos (R$)
            CellStyle valorStyle = workbook.createCellStyle();
            valorStyle.setBorderBottom(BorderStyle.THIN);
            valorStyle.setBorderLeft(BorderStyle.THIN);
            valorStyle.setBorderRight(BorderStyle.THIN);
            valorStyle.setBorderTop(BorderStyle.THIN);
            valorStyle.setAlignment(HorizontalAlignment.RIGHT);
            DataFormat format = workbook.createDataFormat();
            valorStyle.setDataFormat(format.getFormat("R$ #,##0.00"));

            // Positivo (verde)
            CellStyle positivoStyle = workbook.createCellStyle();
            positivoStyle.cloneStyleFrom(valorStyle);
            Font fontPositivo = workbook.createFont();
            fontPositivo.setColor(IndexedColors.GREEN.getIndex());
            positivoStyle.setFont(fontPositivo);

            // Negativo (vermelho)
            CellStyle negativoStyle = workbook.createCellStyle();
            negativoStyle.cloneStyleFrom(valorStyle);
            Font fontNegativo = workbook.createFont();
            fontNegativo.setColor(IndexedColors.RED.getIndex());
            negativoStyle.setFont(fontNegativo);

            // Linha de Total
            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalStyle.setBorderTop(BorderStyle.DOUBLE);
            totalStyle.setBorderBottom(BorderStyle.DOUBLE);

            CellStyle totalValorStyle = workbook.createCellStyle();
            totalValorStyle.cloneStyleFrom(totalStyle);
            totalValorStyle.setBorderLeft(BorderStyle.THIN);
            totalValorStyle.setBorderRight(BorderStyle.THIN);
            totalValorStyle.setAlignment(HorizontalAlignment.RIGHT);
            totalValorStyle.setDataFormat(format.getFormat("R$ #,##0.00"));

            CellStyle totalLabelStyle = workbook.createCellStyle();
            totalLabelStyle.cloneStyleFrom(totalStyle);
            totalLabelStyle.setBorderLeft(BorderStyle.THIN);
            totalLabelStyle.setBorderRight(BorderStyle.THIN);
            totalLabelStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle totalMergedValueStyle = workbook.createCellStyle();
            totalMergedValueStyle.cloneStyleFrom(totalStyle);
            totalMergedValueStyle.setBorderLeft(BorderStyle.THIN);
            totalMergedValueStyle.setBorderRight(BorderStyle.THIN);

            // ===== CONTEÚDO =====

            // Congela cabeçalho
            sheet.createFreezePane(0, 1);

            // Cabeçalho
            Row header = sheet.createRow(0);
            String[] colunas = { "Data", "Categoria", "Valor", "Descrição", "Pagamento" };
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Preencher os dados
            int linha = 1;
            for (Relatorio r : relatorios) {
                Row row = sheet.createRow(linha++);

                Cell cellData = row.createCell(0);
                cellData.setCellValue(r.getData().toString());
                cellData.setCellStyle(dataStyle);

                Cell cellCategoria = row.createCell(1);
                cellCategoria.setCellValue(r.getNomeCategoria() != null ? r.getNomeCategoria() : r.getTipo());
                cellCategoria.setCellStyle(dataStyle);

                Cell cellValor = row.createCell(2);
                BigDecimal valor = r.getValor();
                cellValor.setCellValue(valor.doubleValue());
                cellValor.setCellStyle(valor.doubleValue() >= 0 ? positivoStyle : negativoStyle);

                Cell cellDescricao = row.createCell(3);
                cellDescricao.setCellValue(r.getDescricao() != null ? r.getDescricao() : "");
                cellDescricao.setCellStyle(dataStyle);

                Cell cellPagamento = row.createCell(4);
                cellPagamento.setCellValue(r.getPagamento() != null ? r.getPagamento() : "");
                cellPagamento.setCellStyle(dataStyle);
            }

            // Linha de Total
            int linhaTotal = linha;
            sheet.addMergedRegion(new CellRangeAddress(linhaTotal, linhaTotal, 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(linhaTotal, linhaTotal, 2, 4));

            Row totalRow = sheet.createRow(linhaTotal);

            Cell cellTotalLabel = totalRow.createCell(0);
            cellTotalLabel.setCellValue("Total:");
            cellTotalLabel.setCellStyle(totalLabelStyle);
            totalRow.createCell(1).setCellStyle(totalLabelStyle);

            Cell cellTotalValue = totalRow.createCell(2);
            String formula = "SUM(C2:C" + linhaTotal + ")";
            cellTotalValue.setCellFormula(formula);
            cellTotalValue.setCellStyle(totalValorStyle);

            totalRow.createCell(3).setCellStyle(totalMergedValueStyle);
            totalRow.createCell(4).setCellStyle(totalMergedValueStyle);

            // Auto ajuste colunas
            for (int i = 0; i < colunas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Exporta em memória
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar o arquivo Excel: " + e.getMessage(), e);
        }
    }
}
