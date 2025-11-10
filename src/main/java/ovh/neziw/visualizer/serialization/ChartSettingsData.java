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
package ovh.neziw.visualizer.serialization;

public class ChartSettingsData {

    private double paddingPercent = 0.1;
    private int dataPointColorRgb = 0x0000FF;
    private int regressionLineColorRgb = 0xFF0000;
    private String pointShapeType = "CIRCLE";
    private boolean dashedLine = false;

    public ChartSettingsData() {
    }

    public ChartSettingsData(final double paddingPercent, final int dataPointColorRgb,
                             final int regressionLineColorRgb, final String pointShapeType,
                             final boolean dashedLine) {
        this.paddingPercent = paddingPercent;
        this.dataPointColorRgb = dataPointColorRgb;
        this.regressionLineColorRgb = regressionLineColorRgb;
        this.pointShapeType = pointShapeType;
        this.dashedLine = dashedLine;
    }

    public double getPaddingPercent() {
        return this.paddingPercent;
    }

    public void setPaddingPercent(final double paddingPercent) {
        this.paddingPercent = paddingPercent;
    }

    public int getDataPointColorRgb() {
        return this.dataPointColorRgb;
    }

    public void setDataPointColorRgb(final int dataPointColorRgb) {
        this.dataPointColorRgb = dataPointColorRgb;
    }

    public int getRegressionLineColorRgb() {
        return this.regressionLineColorRgb;
    }

    public void setRegressionLineColorRgb(final int regressionLineColorRgb) {
        this.regressionLineColorRgb = regressionLineColorRgb;
    }

    public String getPointShapeType() {
        return this.pointShapeType;
    }

    public void setPointShapeType(final String pointShapeType) {
        this.pointShapeType = pointShapeType;
    }

    public boolean isDashedLine() {
        return this.dashedLine;
    }

    public void setDashedLine(final boolean dashedLine) {
        this.dashedLine = dashedLine;
    }
}

