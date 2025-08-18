package br.com.controle_despesas.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.controle_despesas.model.Relatorio;

public class RelatorioExcelExporter {

    public static void exportarParaExcel(List<Relatorio> relatorios, String nomeArquivo) {
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

            // Estilo para valores positivos (verde)
            CellStyle positivoStyle = workbook.createCellStyle();
            positivoStyle.cloneStyleFrom(valorStyle);
            Font fontPositivo = workbook.createFont();
            fontPositivo.setColor(IndexedColors.GREEN.getIndex());
            positivoStyle.setFont(fontPositivo);

            // Estilo para valores negativos (vermelho)
            CellStyle negativoStyle = workbook.createCellStyle();
            negativoStyle.cloneStyleFrom(valorStyle);
            Font fontNegativo = workbook.createFont();
            fontNegativo.setColor(IndexedColors.RED.getIndex());
            negativoStyle.setFont(fontNegativo);

            // Estilo para a linha de total
            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalStyle.setBorderTop(BorderStyle.DOUBLE);
            totalStyle.setBorderBottom(BorderStyle.DOUBLE);

            // NOVO ESTILO: Estilo para a célula do valor total, com bordas laterais finas
            CellStyle totalValorStyle = workbook.createCellStyle();
            totalValorStyle.cloneStyleFrom(totalStyle);
            totalValorStyle.setBorderLeft(BorderStyle.THIN);
            totalValorStyle.setBorderRight(BorderStyle.THIN);
            totalValorStyle.setAlignment(HorizontalAlignment.RIGHT);
            totalValorStyle.setDataFormat(format.getFormat("R$ #,##0.00"));

            // NOVO ESTILO: Estilo para células mescladas de total (rótulo)
            CellStyle totalLabelStyle = workbook.createCellStyle();
            totalLabelStyle.cloneStyleFrom(totalStyle);
            totalLabelStyle.setBorderLeft(BorderStyle.THIN);
            totalLabelStyle.setBorderRight(BorderStyle.THIN);
            totalLabelStyle.setAlignment(HorizontalAlignment.CENTER);

            // Estilo para as células vazias da área de valor mesclada
            CellStyle totalMergedValueStyle = workbook.createCellStyle();
            totalMergedValueStyle.cloneStyleFrom(totalStyle);
            totalMergedValueStyle.setBorderLeft(BorderStyle.THIN);
            totalMergedValueStyle.setBorderRight(BorderStyle.THIN);

            // Congelar painel (cabeçalho fixo)
            sheet.createFreezePane(0, 1);

            // Cabeçalho
            Row header = sheet.createRow(0);
            String[] colunas = { "Data", "Categoria", "Valor", "Descrição", "Pagamento" };
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Preenchendo os dados
            int linha = 1;

            for (Relatorio r : relatorios) {
                Row row = sheet.createRow(linha++);

                Cell cellData = row.createCell(0);
                cellData.setCellValue(r.getData().toString());
                cellData.setCellStyle(dataStyle);

                Cell cellCategoria = row.createCell(1);
                cellCategoria.setCellValue(r.getNomeCategoria());
                cellCategoria.setCellStyle(dataStyle);

                Cell cellValor = row.createCell(2);
                BigDecimal valor = r.getValor();
                cellValor.setCellValue(valor.doubleValue());
                if (valor.doubleValue() >= 0) {
                    cellValor.setCellStyle(positivoStyle);
                } else {
                    cellValor.setCellStyle(negativoStyle);
                }

                Cell cellDescricao = row.createCell(3);
                cellDescricao.setCellValue(r.getDescricao() != null ? r.getDescricao() : "");
                cellDescricao.setCellStyle(dataStyle);

                Cell cellPagamento = row.createCell(4);
                cellPagamento.setCellValue(r.getPagamento() != null ? r.getPagamento() : "");
                cellPagamento.setCellStyle(dataStyle);
            }

            // --- Lógica para o Total ---

            // Salva o número da linha para uso nas fórmulas e mesclagens
            int linhaTotal = linha;

            // Mescla as células para o rótulo "Total:"
            sheet.addMergedRegion(new CellRangeAddress(linhaTotal, linhaTotal, 0, 1));

            // Mescla as células para o valor total
            sheet.addMergedRegion(new CellRangeAddress(linhaTotal, linhaTotal, 2, 4));

            Row totalRow = sheet.createRow(linhaTotal);

            // Células para o rótulo "Total:"
            Cell cellTotalLabel = totalRow.createCell(0);
            cellTotalLabel.setCellValue("Total:");
            cellTotalLabel.setCellStyle(totalLabelStyle);
            totalRow.createCell(1).setCellStyle(totalLabelStyle); // Célula vazia mesclada

            // Células para o valor total
            Cell cellTotalValue = totalRow.createCell(2);
            String formula = "SUM(C2:C" + linhaTotal + ")";
            cellTotalValue.setCellFormula(formula);
            cellTotalValue.setCellStyle(totalValorStyle);

            totalRow.createCell(3).setCellStyle(totalMergedValueStyle); // Célula vazia mesclada
            totalRow.createCell(4).setCellStyle(totalMergedValueStyle); // Célula vazia mesclada

            // Ajusta a largura das colunas

            for (int i = 0; i < colunas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escreve o arquivo diretamente no disco
            try (FileOutputStream fileOut = new FileOutputStream(nomeArquivo)) {
                workbook.write(fileOut);
            }

            System.out.println("✅ Relatório exportado para: " + nomeArquivo);

        } catch (IOException e) {
            System.err.println("Erro ao gerar o arquivo Excel: " + e.getMessage());
        }
    }
}