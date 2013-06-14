
/*****************************************************************************
 * Copyright (C) 2004 Elie Naullleau / Semiosys SARL - France                *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-jmatrixview.txt file.                                             *
 *****************************************************************************/
/*
 * Sorting.java
 *
 * Created on 3 septembre 2004, 10:41
 */

package gui.jmatrixview;
import cern.colt.function.IntComparator;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.doublealgo.DoubleMatrix1DComparator;
/**
 *
 * @author  semiosys
 */
public class MatrixSorting {
    
    /** Creates a new instance of Sorting */
    protected MatrixSorting() {
    }
    
    
    protected static void runSort(int[] a, int fromIndex, int toIndex, IntComparator c) {
        cern.colt.Sorting.quickSort(a,fromIndex,toIndex,c);
    }
    protected static void runSort(int fromIndex, int toIndex, IntComparator c, cern.colt.Swapper swapper) {
        cern.colt.GenericSorting.quickSort(fromIndex, toIndex, c, swapper);
    }
    
    
    
    
    
    public static DoubleMatrix2D sort(final DoubleMatrix2D matrix,
    final DoubleMatrix1DComparator c,
    int startRow,
    int endRow) {
        int[] rowIndexes = new int[matrix.rows()]; // row indexes to reorder instead of matrix itself
        for (int i=rowIndexes.length; --i >= 0; ) rowIndexes[i] = i;
        
        final DoubleMatrix1D[] views = new DoubleMatrix1D[matrix.rows()]; // precompute views for speed
        for (int i=views.length; --i >= 0; ) views[i] = matrix.viewRow(i);
        
        IntComparator comp = new IntComparator() {
            public int compare(int a, int b) {
                //return c.compare(matrix.viewRow(a), matrix.viewRow(b));
                return c.compare(views[a], views[b]);
            }
        };
        
        runSort(rowIndexes,startRow,endRow,comp);
        
        // view the matrix according to the reordered row indexes
        // take all columns in the original order
        return matrix.viewSelection(rowIndexes,null);
    }
    
    /** adapatation semiosys - Elie Naulleau */
    public static DoubleMatrix2D sort(final DoubleMatrix2D matrix,int[] rowIndexes,
    final DoubleMatrix1DComparator c,
    int startRow,
    int endRow) {
        
        if(rowIndexes==null) {
            rowIndexes = new int[matrix.rows()]; // row indexes to reorder instead of matrix itself
            for (int i=rowIndexes.length; --i >= 0; ) rowIndexes[i] = i;
        }
        final DoubleMatrix1D[] views = new DoubleMatrix1D[matrix.rows()]; // precompute views for speed
        for (int i=views.length; --i >= 0; ) views[i] = matrix.viewRow(i);
        
        IntComparator comp = new IntComparator() {
            public int compare(int a, int b) {
                //return c.compare(matrix.viewRow(a), matrix.viewRow(b));
                return c.compare(views[a], views[b]);
            }
        };
        
        runSort(rowIndexes,startRow,endRow,comp);
        
        // view the matrix according to the reordered row indexes
        // take all columns in the original order
        return matrix.viewSelection(rowIndexes,null);
    }
    
    
    /** adapatation semiosys - Elie Naulleau */
    public static DoubleMatrix2D sort(
    final DoubleMatrix2D matrix,
    final String[] labels,
    String[] labelsOut,
    final int[] rowTypes,
    int [] sortedRowTypes,
    final DoubleMatrix1DComparator c,
    final int startRow,
    final int endRow) {
        
        
        int [] rowIndexes = new int[matrix.rows()]; // row indexes to reorder instead of matrix itself
        for (int i=rowIndexes.length; --i >= 0; ) rowIndexes[i] = i;
        
        final DoubleMatrix1D[] views = new DoubleMatrix1D[matrix.rows()]; // precompute views for speed
        for (int i=views.length; --i >= 0; ) views[i] = matrix.viewRow(i);
        
        IntComparator comp = new IntComparator() {
            public int compare(int a, int b) {
                //return c.compare(matrix.viewRow(a), matrix.viewRow(b));
                return c.compare(views[a], views[b]);
            }
        };
        
        runSort(rowIndexes,startRow,endRow,comp);
        
        if(rowTypes!=null)
            for(int i=0; i<matrix.rows(); i++) {
                sortedRowTypes[i] = rowTypes[rowIndexes[i]];
            }
        
        if(labels!=null)
            for(int i=0; i<matrix.rows(); i++) {
                labelsOut[i] = labels[rowIndexes[i]];
            }
        // view the matrix according to the reordered row indexes
        // take all columns in the original order
        return matrix.viewSelection(rowIndexes,null);
    }
    
    
    
    
    
    
    
}
