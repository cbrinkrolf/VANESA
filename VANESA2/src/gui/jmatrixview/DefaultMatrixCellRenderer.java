
/*****************************************************************************
 * Copyright (C) 2004 Elie Naullleau / Semiosys SARL - France                *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-jmatrixview.txt file.                                             *
 *****************************************************************************/

/*
 * DefaultMatrixCellRenderer.java
 *
 * Created on 19 octobre 2004, 14:30
 */

package gui.jmatrixview;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * An exemple of matrix cell renderer.
 * Colorization strategy is taken from Jean Daniel Fekete Infovis Project.
 * @author Elie Naulleau, semiosys
 */
public class DefaultMatrixCellRenderer implements MatrixCellRenderer {
    private DoubleMatrix2D matrix;
    private final Rectangle2D.Double rectangle = new Rectangle2D.Double();
    private final RoundRectangle2D.Double roundRectangle = new RoundRectangle2D.Double();
    private final Ellipse2D.Double elipse = new Ellipse2D.Double();
    private  Polygon triangle;
    private double startRed;
    private double startGreen;
    private double startBlue;
    private double endRed;
    private double endGreen;
    private double endBlue;
    private double min,max, scale;
    private double alpha = 50;
    private DecimalFormat format;
    private int [] rowTypes;
    private int [] colTypes;
    /** Creates a new instance of DefaultMatrixCellRenderer */
    public DefaultMatrixCellRenderer(  DoubleMatrix2D matrix) {
        this(matrix, Color.white, Color.blue, 0, 1, null, null);
    }
    /** Creates a new instance of DefaultMatrixCellRenderer */
    public DefaultMatrixCellRenderer(  DoubleMatrix2D matrix, Color start, Color end) {
        this(matrix, start, end, 0, 1, null, null);
    }
    
    /** Creates a new instance of DefaultMatrixCellRenderer */
    public DefaultMatrixCellRenderer(  DoubleMatrix2D matrix, Color start, Color end, double min, double max, int []rt, int []ct) {
        this.matrix = matrix;
        rowTypes = rt;
        colTypes = ct;
        startRed = start.getRed() / 255.0;
        startGreen = start.getGreen() / 255.0;
        startBlue = start.getBlue() / 255.0;
        endRed = end.getRed() / 255.0;
        endGreen = end.getGreen() / 255.0;
        endBlue = end.getBlue() / 255.0;
        this.min = min;
        this.max = max;
        scale = ((max-min) == 0) ? 1 : 1.0 / (max-min);
        format = new DecimalFormat("##.##");
    }
    
    public java.awt.Color getColor(int row, int col) {
        double value = matrix.getQuick(row, col);
        double t = (value - min) * scale;
        if (Double.isNaN(t)) t = min;
        
        double red = (1 - t) * startRed + t * endRed;
        double green = (1 - t) * startGreen + t * endGreen;
        double blue = (1 - t) * startBlue + t * endBlue;
        
        return new Color( ((((int)(alpha * 255.999)) & 0xFF) << 24) |
        ((((int)(red * 255.999)) & 0xFF) << 16) |
        ((((int)(green * 255.999)) & 0xFF) << 8) |
        ((((int)(blue * 255.999)) & 0xFF) << 0), true );
    }
    
    public java.awt.Shape getShape(int row, int col, int x, int y, int w, int h) {
//        if(rowTypes!=null) {
//           //System.err.println("rowTypes["+row+"]"+rowTypes[row] + "colTypes["+col+"]"+colTypes[col]);
//           
//            switch(colTypes[col]) {
//                case 0: {
//                    
//                    rectangle.setRect(x, y, w, h);
//                    return rectangle;
//                }
//                case 1: {
//                    roundRectangle.setRoundRect(x, y, w, h, 10, 15 );
//                    return roundRectangle;
//                }
//                case 2: {
//                    elipse.setFrame(x, y, w, h);
//                    return elipse;
//                }
//                
//                case 3: {
//                    triangle = new Polygon(new int[] {(x+w/2), x, (x+w)}, new int[] {y, (y+h), (y+h) }, 3);
//                    return triangle;
//                }
//            }
//        }
            rectangle.setRect(x, y, w, h);
            return rectangle;
    }
    
    /**
     * Getter for property matrix.
     * @return Value of property matrix.
     */
    public cern.colt.matrix.DoubleMatrix2D getMatrix() {
        return matrix;
    }
    
    /**
     * Setter for property matrix.
     * @param matrix New value of property matrix.
     */
    public void setMatrix(cern.colt.matrix.DoubleMatrix2D matrix) {
        this.matrix = matrix;
        
    }
    
    public String getLabel(int row, int col) {
        return format.format(matrix.getQuick(row, col));
    }
    
    public void setColumnTypes(int[] rt) {
    }
    
    public void setRowTypes(int[] rt) {
    }
    
    /**
     * Getter for property rowTypes.
     * @return Value of property rowTypes.
     */
    public int[] getRowTypes() {
        return this.rowTypes;
    }
    
    /**
     * Getter for property colTypes.
     * @return Value of property colTypes.
     */
    public int[] getColTypes() {
        return this.colTypes;
    }
    
    /**
     * Setter for property colTypes.
     * @param colTypes New value of property colTypes.
     */
    public void setColTypes(int[] colTypes) {
        this.colTypes = colTypes;
    }
    
}
