/*
 * This file is part of "linear-regression-visualizer", licensed under MIT License.
 *
 *  Copyright (c) 2025 neziw
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package ovh.neziw.visualizer.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFScatterChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ovh.neziw.visualizer.DataTableModel;
import ovh.neziw.visualizer.LinearRegressionCalculator;

public class ExcelExporter {

    public void exportToExcel(final FileOutputStream outputStream,
                              final List<DataTableModel.DataPoint> dataPoints) throws IOException {
        try (final XSSFWorkbook workbook = new XSSFWorkbook()) {
            final XSSFSheet dataSheet = workbook.createSheet("Dane");
            final int dataEndRow = this.writeDataSheet(dataSheet, dataPoints, workbook);
            final List<DataTableModel.DataPoint> validPoints = this.getValidDataPoints(dataPoints);
            if (validPoints.size() >= 2) {
                final LinearRegressionCalculator.RegressionResult regression =
                    LinearRegressionCalculator.calculate(validPoints);
                if (regression != null) {
                    final Sheet regressionSheet = workbook.createSheet("Regresja");
                    this.writeRegressionSheet(regressionSheet, regression, validPoints, workbook);
                    final int validDataStartRow = this.writeValidDataPoints(dataSheet, validPoints, dataEndRow + 3, workbook);
                    final int regressionDataStartRow = validDataStartRow + validPoints.size() + 3;
                    final int regressionDataEndRow = this.writeRegressionLineData(
                        dataSheet, regression, validPoints, regressionDataStartRow, workbook);

                    this.createChart(dataSheet, validDataStartRow, validDataStartRow + validPoints.size() - 1,
                        regressionDataStartRow + 1, regressionDataEndRow);
                }
            }

            workbook.write(outputStream);
        }
    }

    private int writeDataSheet(final Sheet sheet,
                               final List<DataTableModel.DataPoint> dataPoints,
                               final Workbook workbook) {
        final CellStyle headerStyle = this.createHeaderStyle(workbook);

        final Row headerRow = sheet.createRow(0);
        final Cell xHeader = headerRow.createCell(0);
        xHeader.setCellValue("X");
        xHeader.setCellStyle(headerStyle);
        final Cell yHeader = headerRow.createCell(1);
        yHeader.setCellValue("Y");
        yHeader.setCellStyle(headerStyle);

        int rowNum = 1;
        for (final DataTableModel.DataPoint point : dataPoints) {
            final Row row = sheet.createRow(rowNum++);
            if (point.getX() != null) {
                row.createCell(0).setCellValue(point.getX());
            } else {
                row.createCell(0).setCellValue("");
            }
            if (point.getY() != null) {
                row.createCell(1).setCellValue(point.getY());
            } else {
                row.createCell(1).setCellValue("");
            }
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        return rowNum - 1;
    }

    private int writeValidDataPoints(final XSSFSheet sheet,
                                     final List<DataTableModel.DataPoint> validPoints,
                                     final int startRow,
                                     final Workbook workbook) {
        final CellStyle headerStyle = this.createHeaderStyle(workbook);
        final Row headerRow = sheet.createRow(startRow);
        final Cell xHeader = headerRow.createCell(3);
        xHeader.setCellValue("X (dane)");
        xHeader.setCellStyle(headerStyle);
        final Cell yHeader = headerRow.createCell(4);
        yHeader.setCellValue("Y (dane)");
        yHeader.setCellStyle(headerStyle);
        int rowNum = startRow + 1;
        for (final DataTableModel.DataPoint point : validPoints) {
            final Row row = sheet.createRow(rowNum++);
            row.createCell(3).setCellValue(point.getX());
            row.createCell(4).setCellValue(point.getY());
        }
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        return startRow + 1;
    }

    private int writeRegressionLineData(final XSSFSheet sheet,
                                        final LinearRegressionCalculator.RegressionResult regression,
                                        final List<DataTableModel.DataPoint> validPoints,
                                        final int startRow,
                                        final Workbook workbook) {
        final CellStyle headerStyle = this.createHeaderStyle(workbook);
        final Row headerRow = sheet.createRow(startRow);
        final Cell xHeader = headerRow.createCell(3);
        xHeader.setCellValue("X (regresja)");
        xHeader.setCellStyle(headerStyle);
        final Cell yHeader = headerRow.createCell(4);
        yHeader.setCellValue("Y (regresja)");
        yHeader.setCellStyle(headerStyle);
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        for (final DataTableModel.DataPoint point : validPoints) {
            final double x = point.getX();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
        }
        double rangeX = maxX - minX;
        if (rangeX == 0) {
            rangeX = Math.max(Math.abs(minX), 1.0);
        }
        final double paddingX = rangeX * 0.1;
        final double startX = minX - paddingX;
        final double endX = maxX + paddingX;

        final int pointsCount = 50;
        int rowNum = startRow + 1;
        for (int i = 0; i <= pointsCount; i++) {
            final double x = startX + (endX - startX) * i / pointsCount;
            final double y = regression.predict(x);
            final Row row = sheet.createRow(rowNum++);
            row.createCell(3).setCellValue(x);
            row.createCell(4).setCellValue(y);
        }
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        return rowNum - 1;
    }

    private void writeRegressionSheet(final Sheet sheet,
                                      final LinearRegressionCalculator.RegressionResult regression,
                                      final List<DataTableModel.DataPoint> validPoints,
                                      final Workbook workbook) {
        final CellStyle headerStyle = this.createHeaderStyle(workbook);
        final CellStyle valueStyle = this.createValueStyle(workbook);

        int rowNum = 0;

        final Row paramRow = sheet.createRow(rowNum++);
        final Cell paramHeader = paramRow.createCell(0);
        paramHeader.setCellValue("Parametr");
        paramHeader.setCellStyle(headerStyle);
        final Cell paramValue = paramRow.createCell(1);
        paramValue.setCellValue("Wartość");
        paramValue.setCellStyle(headerStyle);

        rowNum++;
        final Row interceptRow = sheet.createRow(rowNum++);
        interceptRow.createCell(0).setCellValue("Wyraz wolny (a)");
        final Cell interceptCell = interceptRow.createCell(1);
        interceptCell.setCellValue(regression.getIntercept());
        interceptCell.setCellStyle(valueStyle);

        final Row slopeRow = sheet.createRow(rowNum++);
        slopeRow.createCell(0).setCellValue("Współczynnik kierunkowy (b)");
        final Cell slopeCell = slopeRow.createCell(1);
        slopeCell.setCellValue(regression.getSlope());
        slopeCell.setCellStyle(valueStyle);

        final double rSquared = this.calculateRSquared(regression, validPoints);
        final Row rSquaredRow = sheet.createRow(rowNum++);
        rSquaredRow.createCell(0).setCellValue("R²");
        final Cell rSquaredCell = rSquaredRow.createCell(1);
        rSquaredCell.setCellValue(rSquared);
        rSquaredCell.setCellStyle(valueStyle);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private double calculateRSquared(final LinearRegressionCalculator.RegressionResult regression,
                                     final List<DataTableModel.DataPoint> validPoints) {
        if (validPoints.isEmpty()) {
            return 0.0;
        }
        double meanY = 0.0;
        for (final DataTableModel.DataPoint point : validPoints) {
            meanY += point.getY();
        }
        meanY /= validPoints.size();
        double ssTotal = 0.0;
        double ssResidual = 0.0;

        for (final DataTableModel.DataPoint point : validPoints) {
            final double y = point.getY();
            final double yPredicted = regression.predict(point.getX());
            ssTotal += Math.pow(y - meanY, 2);
            ssResidual += Math.pow(y - yPredicted, 2);
        }
        if (ssTotal == 0.0) {
            return 0.0;
        }
        return 1.0 - (ssResidual / ssTotal);
    }

    private CellStyle createHeaderStyle(final Workbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        final Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createValueStyle(final Workbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.0000"));
        return style;
    }

    private void createChart(final XSSFSheet sheet,
                             final int dataStartRow,
                             final int dataEndRow,
                             final int regressionStartRow,
                             final int regressionEndRow) {
        final XSSFDrawing drawing = sheet.createDrawingPatriarch();
        final XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 6, 0, 20, 25);

        final XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Regresja liniowa");
        chart.setTitleOverlay(false);

        final XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        final XDDFValueAxis bottomAxis = chart.createValueAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("X");
        bottomAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        final XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Y");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        final XDDFDataSource<Double> dataX = XDDFDataSourcesFactory.fromNumericCellRange(
            sheet, new CellRangeAddress(dataStartRow, dataEndRow, 3, 3));
        final XDDFNumericalDataSource<Double> dataY = XDDFDataSourcesFactory.fromNumericCellRange(
            sheet, new CellRangeAddress(dataStartRow, dataEndRow, 4, 4));

        final XDDFScatterChartData scatterData = (XDDFScatterChartData) chart.createData(
            ChartTypes.SCATTER, bottomAxis, leftAxis);

        final XDDFScatterChartData.Series dataSeries = (XDDFScatterChartData.Series) scatterData.addSeries(dataX, dataY);
        dataSeries.setTitle("Dane", null);
        dataSeries.setMarkerStyle(MarkerStyle.CIRCLE);

        final XDDFDataSource<Double> regressionX = XDDFDataSourcesFactory.fromNumericCellRange(
            sheet, new CellRangeAddress(regressionStartRow, regressionEndRow, 3, 3));
        final XDDFNumericalDataSource<Double> regressionY = XDDFDataSourcesFactory.fromNumericCellRange(
            sheet, new CellRangeAddress(regressionStartRow, regressionEndRow, 4, 4));

        final XDDFScatterChartData.Series regressionSeries = (XDDFScatterChartData.Series) scatterData.addSeries(regressionX, regressionY);
        regressionSeries.setTitle("Regresja", null);
        regressionSeries.setMarkerStyle(MarkerStyle.NONE);

        chart.plot(scatterData);
    }

    private List<DataTableModel.DataPoint> getValidDataPoints(final List<DataTableModel.DataPoint> dataPoints) {
        return dataPoints.stream()
            .filter(point -> point.getX() != null && point.getY() != null)
            .map(point -> new DataTableModel.DataPoint(point.getX(), point.getY()))
            .collect(java.util.stream.Collectors.toList());
    }
}
