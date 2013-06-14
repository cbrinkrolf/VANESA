
/*****************************************************************************
 * Copyright (C) 2004 Elie Naullleau / Semiosys SARL - France                *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-jmatrixview.txt file.                                             *
 *****************************************************************************/
/*
 * MatrixCellRenderer.java
 *
 * Created on 19 octobre 2004, 14:28
 */

package gui.jmatrixview;
import cern.colt.matrix.DoubleMatrix2D;
/**
 *
 * @author  semiosys
 */
public interface MatrixCellRenderer {
    
    public java.awt.Shape getShape(int row, int col, int x, int y, int w, int h);
    public java.awt.Color getColor(int row, int col);
    public String getLabel(int row, int col);
    public void setMatrix(DoubleMatrix2D m);
    public void setRowTypes(int [] rt);
    public void setColumnTypes(int [] rt);
}
