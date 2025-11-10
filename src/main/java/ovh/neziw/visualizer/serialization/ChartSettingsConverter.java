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

import java.awt.Color;
import java.awt.Shape;
import ovh.neziw.visualizer.gui.ChartSettings;

public final class ChartSettingsConverter {

    public static ChartSettingsData toData(final ChartSettings settings) {
        return new ChartSettingsData(
            settings.getPaddingPercent(),
            settings.getDataPointColor().getRGB(),
            settings.getRegressionLineColor().getRGB(),
            shapeToString(settings.getDataPointShape()),
            settings.isDashedLine()
        );
    }

    public static void applyToSettings(final ChartSettingsData data, final ChartSettings settings) {
        settings.setPaddingPercent(data.getPaddingPercent());
        settings.setDataPointColor(new Color(data.getDataPointColorRgb(), true));
        settings.setRegressionLineColor(new Color(data.getRegressionLineColorRgb(), true));
        settings.setDataPointShape(stringToShape(data.getPointShapeType()));
        settings.setDashedLine(data.isDashedLine());
    }

    private static String shapeToString(final Shape shape) {
        if (shape == null) {
            return "CIRCLE";
        }
        final String className = shape.getClass().getSimpleName();
        switch (className) {
            case "Ellipse2D$Double":
                return "CIRCLE";
            case "Rectangle2D$Double":
                final java.awt.geom.Rectangle2D rect = (java.awt.geom.Rectangle2D) shape;
                if (rect.getWidth() > 6.5) {
                    return "SQUARE_LARGE";
                }
                return "SQUARE";
            case "Path2D$Double":
                if (isDiamond(shape)) {
                    return "DIAMOND";
                } else if (isTriangle(shape)) {
                    return "TRIANGLE";
                } else if (isStar(shape)) {
                    return "STAR";
                }
                break;
        }
        return "CIRCLE";
    }

    private static Shape stringToShape(final String shapeType) {
        if (shapeType == null) {
            return createCircle();
        }
        switch (shapeType) {
            case "SQUARE":
                return new java.awt.geom.Rectangle2D.Double(-3, -3, 6, 6);
            case "SQUARE_LARGE":
                return new java.awt.geom.Rectangle2D.Double(-4, -4, 8, 8);
            case "DIAMOND":
                return createDiamond();
            case "TRIANGLE":
                return createTriangle();
            case "STAR":
                return createStar();
            default:
                return createCircle();
        }
    }

    private static Shape createCircle() {
        return new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6);
    }

    private static Shape createDiamond() {
        final java.awt.geom.Path2D.Double diamond = new java.awt.geom.Path2D.Double();
        diamond.moveTo(0, -4);
        diamond.lineTo(4, 0);
        diamond.lineTo(0, 4);
        diamond.lineTo(-4, 0);
        diamond.closePath();
        return diamond;
    }

    private static Shape createTriangle() {
        final java.awt.geom.Path2D.Double triangle = new java.awt.geom.Path2D.Double();
        triangle.moveTo(0, -4);
        triangle.lineTo(-4, 4);
        triangle.lineTo(4, 4);
        triangle.closePath();
        return triangle;
    }

    private static Shape createStar() {
        final java.awt.geom.Path2D.Double star = new java.awt.geom.Path2D.Double();
        final int outerRadius = 4;
        final int innerRadius = 2;
        final int points = 5;
        for (int i = 0; i < points * 2; i++) {
            final double angle = Math.PI * i / points - Math.PI / 2;
            final int radius = i % 2 == 0 ? outerRadius : innerRadius;
            final double x = Math.cos(angle) * radius;
            final double y = Math.sin(angle) * radius;
            if (i == 0) {
                star.moveTo(x, y);
            } else {
                star.lineTo(x, y);
            }
        }
        star.closePath();
        return star;
    }

    private static boolean isDiamond(final Shape shape) {
        if (!(shape instanceof java.awt.geom.Path2D.Double)) {
            return false;
        }
        final java.awt.geom.Path2D.Double path = (java.awt.geom.Path2D.Double) shape;
        final java.awt.geom.PathIterator it = path.getPathIterator(null);
        final double[] coords = new double[6];
        int pointCount = 0;
        while (!it.isDone()) {
            final int type = it.currentSegment(coords);
            if (type == java.awt.geom.PathIterator.SEG_MOVETO || type == java.awt.geom.PathIterator.SEG_LINETO) {
                pointCount++;
            }
            it.next();
        }
        return pointCount == 4;
    }

    private static boolean isTriangle(final Shape shape) {
        if (!(shape instanceof java.awt.geom.Path2D.Double)) {
            return false;
        }
        final java.awt.geom.Path2D.Double path = (java.awt.geom.Path2D.Double) shape;
        final java.awt.geom.PathIterator it = path.getPathIterator(null);
        final double[] coords = new double[6];
        int pointCount = 0;
        while (!it.isDone()) {
            final int type = it.currentSegment(coords);
            if (type == java.awt.geom.PathIterator.SEG_MOVETO || type == java.awt.geom.PathIterator.SEG_LINETO) {
                pointCount++;
            }
            it.next();
        }
        return pointCount == 3;
    }

    private static boolean isStar(final Shape shape) {
        if (!(shape instanceof java.awt.geom.Path2D.Double)) {
            return false;
        }
        final java.awt.geom.Path2D.Double path = (java.awt.geom.Path2D.Double) shape;
        final java.awt.geom.PathIterator it = path.getPathIterator(null);
        final double[] coords = new double[6];
        int pointCount = 0;
        while (!it.isDone()) {
            final int type = it.currentSegment(coords);
            if (type == java.awt.geom.PathIterator.SEG_MOVETO || type == java.awt.geom.PathIterator.SEG_LINETO) {
                pointCount++;
            }
            it.next();
        }
        return pointCount == 10;
    }
}
