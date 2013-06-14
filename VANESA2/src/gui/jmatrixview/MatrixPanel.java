/*
 * MatrixPanel.java
 *
 * Created on 9 mars 2004, 21:04
 */


/*****************************************************************************
 * Copyright (C) 2004 Elie Naullleau / Semiosys SARL - France                *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-jmatrixview.txt file.                                             *
 *****************************************************************************/

package gui.jmatrixview;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.doublealgo.DoubleMatrix1DComparator;
import cern.colt.matrix.doublealgo.Statistic;

/**
 * On a une matrice très grande et ce MatrixPanel représente une fenêtre sur cette matrice.
 * La taille visible de cette fenêtre à l'
 * @author  semiosys
 */
public class MatrixPanel extends JPanel implements MouseMotionListener, ActionListener {
    private static final int  MAXIMUM_WIDTH = 10000;
    private static int ROW_LABEL_WIDTH = 50;
    private static int COL_LABEL_WIDTH = 50;
    
    
    
    private Color backgroundColor = Color.white;
    
    private int rowResolution = 6;
    private int colResolution = 6;
    
    private int interColumSpace=1;
    private int interRowSpace=1;
    
    private int visibleColumns=0;
    private int visibleRows=0;
    
    private int matrixStartRow=0;
    private int matrixStartCol=0;
    
    private int width, height;
    private int virtualHeight; // pour le JscrollPane et les scrollbars
    private int virtualWidth;
    
    private Dimension visibleSize;
    private Dimension actualSize;
    
    private BufferedImage offscreenImage;
    private Graphics2D offscreenGraphics;
    private JViewport viewport;
    
    /** Data container */
    private DoubleMatrix2D matrix;
    private String [] rowLabels=null;
    private String [] colLabels=null;
    private int [] rowTypes=null;
    private int [] colTypes=null;
    private JScrollBar vScrollBar;
    private JScrollBar hScrollBar;
    private JPopupMenu popupMenu;
    private Shape shape;
    private boolean rowLabelVisible = true;
    private boolean colLabelVisible = true;
    private boolean paintLabels = true;
    private boolean paintCross = true;
    private boolean paintValues = true;
    private boolean paintHVLines = true;
    private int currentMouseCol=0;
    private int currentMouseRow=0;
    private static Color crossColor = new Color(250,40,10,80);
    
    private Font font = new Font("Serif", Font.PLAIN, 10);
    static java.util.Random random = new java.util.Random();
    private MatrixCellRenderer matrixCellRenderer=null;
    private final static double angle = 90.0 * Math.PI / 180.0;
    
    private ArrayList selectionAreas;
    private Point selectedCell1, selectedCell2;
    
    
    /** Creates a new instance of MatrixPanel */
    public MatrixPanel(DoubleMatrix2D matrix, JViewport vp, JScrollBar hsb, JScrollBar vsb) {
        // Use dummy values at first
        this.setVisible(false);
        this.viewport = vp;
        this.matrix = matrix;
        
        
        setHScrollBar(hsb);
        setVScrollBar(vsb);
        
        updateMaxSize();
        actualSize= new Dimension(width,height);
        setSize(width, height);
        setPreferredSize( actualSize);
        
        createOffscreenImage();
        this.setVisible(true);
        
        this.addMouseMotionListener(this);
        activatePopupMenu();
    }
    
    private void activatePopupMenu() {
        popupMenu  = new JPopupMenu("Sort");
        JMenuItem sortRow = new JMenuItem("Sort rows");
        sortRow.setActionCommand("r");
        popupMenu.add(sortRow);
        popupMenu.add(new JSeparator());
        sortRow.addActionListener(this);
        
        JMenuItem sortCol = new JMenuItem("Sort columns");
        popupMenu.add(sortCol);
        sortCol.setActionCommand("c");
        popupMenu.add(new JSeparator());
        sortCol.addActionListener(this);
        
        
        JMenuItem sortBoth = new JMenuItem("Sort rows & columns");
        popupMenu.add(sortBoth);
        sortBoth.setActionCommand("&");
        popupMenu.add(new JSeparator());
        sortBoth.addActionListener(this);
        
        this.addMouseListener(new PopupListener());
        
    }
    
    public void updateMaxSize() {
        width  = viewport.getExtentSize().width + ROW_LABEL_WIDTH;
        height = viewport.getExtentSize().height + COL_LABEL_WIDTH;
        computeCanvasSpread();
        repaint();
    }
    
    
    
    
    
    private void computeCanvasSpread() {
        
        if(rowResolution<7)
            rowLabelVisible  = false;
        else
            rowLabelVisible = true;
        
        
        if(colResolution<7)
            colLabelVisible  = false;
        else
            colLabelVisible = true;
        
        height = viewport.getExtentSize().height; // Hauteur réelle à l'écran
        width = viewport.getExtentSize().width;
        virtualHeight = (rowResolution+interRowSpace)*matrix.rows(); // Hauteur virtuelle donnée en pature au JScrollPane
        virtualWidth = (colResolution+interColumSpace)*matrix.columns() - ROW_LABEL_WIDTH; // Hauteur virtuelle donnée en pature au JScrollPane
        // Nombre de ligne visible dans l'espace à l'écran
        visibleRows    = java.lang.Math.min((height-ROW_LABEL_WIDTH)/(rowResolution+interRowSpace), matrix.rows());
        visibleColumns = java.lang.Math.min((width-COL_LABEL_WIDTH)/(colResolution+interColumSpace), matrix.columns() );
        
        vScrollBar.setValues(matrixStartRow, visibleRows, 0, matrix.rows());
        hScrollBar.setValues(matrixStartCol, visibleColumns, 0, matrix.columns());
        
        setSize(virtualWidth, virtualHeight); // les scrollbars se dessine en fonction de la taille virtuelle
        actualSize = new Dimension(virtualWidth, virtualHeight);
        setPreferredSize(actualSize);
        
        createOffscreenImage(); // Mais l'image fait width*height
        repaint();
    }
    
    
    
    private void createOffscreenImage() {
        //  width  = Math.min(viewport.getExtentSize().width, virtualWidth);
        //  height = Math.min(viewport.getExtentSize().height, virtualHeight);
        
        if(width==0 || height==0) {
            width  = viewport.getExtentSize().width;
            height = viewport.getExtentSize().height;
        }
        if(width==0 || height==0) {
            width  =  height = 300;
        }
        
        offscreenImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB);
        offscreenGraphics = (Graphics2D) offscreenImage.getGraphics();
    }
    
    
    public BufferedImage getThumbImage(int w, int h) {

        if(offscreenImage==null || w==0 || h==0) return null;
      
        double scaleX = (double)w/(double)width;
        double scaleY = (double)h/(double)height;
        
        AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(scaleX, scaleY), null);
        return op.filter(offscreenImage, null);
      
    }
    
    
    /** Attention il faut repeindre que ce qui est visible à l'écran ...
     * Toute partie cachée doit être ignorée */
    @Override
	public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        offscreenGraphics.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        
        
        // On met tout à blanc
        offscreenGraphics.setColor(backgroundColor);
        //offscreenGraphics.fillRect(0, 0, visibleColumns*(colResolution+interColumSpace), visibleRows*(rowResolution*interRowSpace));
        offscreenGraphics.fillRect(0, 0, width, height);
        offscreenGraphics.setColor(Color.gray);
        
        offscreenGraphics.drawRect(
        ROW_LABEL_WIDTH-1,
        COL_LABEL_WIDTH-1,
        visibleColumns*(colResolution+interColumSpace),
        visibleRows*(rowResolution+interRowSpace));
        
        int x, y;
        int sidx = 0;
        
        
        
        
        if(paintLabels) {
            
            if(colLabelVisible) {
                // Peindre le HEADER des COLONNES
                x = 0;
                y = COL_LABEL_WIDTH-2;
                // System.err.println("matrixStartCol="+matrixStartCol);
                // System.err.println("visibleColumns="+visibleColumns);
                
                int startX = ROW_LABEL_WIDTH;
                for(int c=matrixStartCol; c<matrix.columns() && c<(matrixStartCol+visibleColumns); c++) {
                    
                    //offscreenGraphics.fillRect(startX, y, colResolution, COL_LABEL_WIDTH);
                    // offscreenGraphics.setColor(Color.gray);
                    offscreenGraphics.rotate(-angle, startX, y );
                    //offscreenGraphics.drawRect( startX, y, ROW_LABEL_WIDTH,  colResolution-1);
                    offscreenGraphics.setColor(Color.black);
                    if(colLabels==null)
                        offscreenGraphics.drawString(String.valueOf(c), startX+2,3+y +colResolution/2);
                    else
                        offscreenGraphics.drawString(colLabels[c], startX+2,3+y +colResolution/2);
                    offscreenGraphics.rotate(angle, startX, y);
                    
                    startX += (colResolution+interColumSpace);
                }
            }
            
            if(rowLabelVisible) {
                // Peindre le HEADER DES LIGNES ******************** /
                //                System.err.println("matrixStartRow="+matrixStartRow);
                //                System.err.println("visibleRows="+visibleRows);
                //
                int startY = COL_LABEL_WIDTH;
                for(int r=matrixStartRow; r<matrix.rows() && r<(matrixStartRow+visibleRows); r++) {
                    String str;
                    if(rowLabels==null)
                        str= String.valueOf(r);
                    else
                        str = rowLabels[r];
                    Rectangle2D rec = fm.getStringBounds(str, g2);
                    int startAt = ROW_LABEL_WIDTH - ((int) rec.getWidth()) ;
                    offscreenGraphics.setColor(Color.black);
                    
                    offscreenGraphics.drawString(str, startAt, startY+rowResolution/2+3 );
                    startY += (rowResolution+interRowSpace);
                    
                }
                
            }
        }
        
        
        // On commence à peindre après le header
        y = COL_LABEL_WIDTH;
        //           for(int i=0; i<visibleRows &&(i+matrixStartRow)<matrix.rows() ; i++) {
        //            x = ROW_LABEL_WIDTH;
        //            for(int j=0; j<visibleColumns && (j+matrixStartCol)<matrix.columns(); j++) {
        //                ColumnRenderer CR = model.getRenderer(j);
        //
        for(int i=matrixStartRow; i<(matrixStartRow+visibleRows)&& i<matrix.rows() ; i++) {
            x = ROW_LABEL_WIDTH;
            for(int j=matrixStartCol; j<(matrixStartCol+visibleColumns) && j<matrix.columns(); j++) {
                if(matrixCellRenderer==null) {
                    offscreenGraphics.setColor(Color.getHSBColor( random.nextFloat(), 1.0F, 1.0F ));
                    offscreenGraphics.fillRect(x, y, colResolution, rowResolution);
                }
                else {
                    offscreenGraphics.setColor( matrixCellRenderer.getColor(i, j) );
                    offscreenGraphics.fill( matrixCellRenderer.getShape(i, j, x, y, colResolution, rowResolution) );
                    if(paintValues && colResolution > 15 && rowResolution>7) {
                        offscreenGraphics.setColor(Color.black);
                        offscreenGraphics.drawString( matrixCellRenderer.getLabel(i, j), x+2, y+rowResolution/2+3);
                    }
                }
                if(paintHVLines) {
                    offscreenGraphics.setColor(Color.gray);
                    
                    for(int s=0; s<interColumSpace; s++)
                        offscreenGraphics.drawRect(x-s, y, colResolution+s, rowResolution);
                    
                    for(int s=0; s<interRowSpace; s++)
                        offscreenGraphics.drawRect(x, y-s, colResolution, rowResolution+s);
                    
                    offscreenGraphics.fillRect(x+colResolution, y+rowResolution, interColumSpace, interRowSpace);
                }
                x += (colResolution+interColumSpace);
                sidx++;
            }
            y += (rowResolution+interRowSpace);
        }
        
        if(paintCross) {
            offscreenGraphics.setColor(crossColor);
            y = currentMouseRow*(interRowSpace+rowResolution) + (paintLabels ? COL_LABEL_WIDTH : 0 );
            x = currentMouseCol*(interColumSpace+colResolution) + (paintLabels ? ROW_LABEL_WIDTH : 0 );
            
            offscreenGraphics.fillRect(0, y, width, rowResolution);
            offscreenGraphics.fillRect(x, 0, colResolution, height);
            
        }
        
        
        g2.drawImage(offscreenImage, 0, 0, null);
    }
    
    
    
    
    
    @Override
	public Dimension getSize() {
        return actualSize;
    }
    @Override
	public Dimension getPreferredSize() {
        return actualSize;
    }
    
    /** Getter for property rowResolution.
     * @return Value of property rowResolution.
     *
     */
    public int getRowResolution() {
        return rowResolution;
    }
    
    /** Setter for property rowResolution.
     * @param rowResolution New value of property rowResolution.
     *
     */
    public void setRowResolution(int rowResolution) {
        this.rowResolution = rowResolution;
        computeCanvasSpread();
        
    }
    
    /** Getter for property colResolution.
     * @return Value of property colResolution.
     *
     */
    public int getColResolution() {
        return colResolution;
    }
    
    /** Setter for property colResolution.
     * @param colResolution New value of property colResolution.
     *
     */
    public void setColResolution(int colResolution) {
        this.colResolution = colResolution;
        computeCanvasSpread();
        
    }
    
    
    /** Getter for property interColumSpace.
     * @return Value of property interColumSpace.
     *
     */
    public int getInterColumSpace() {
        return interColumSpace;
    }
    
    
    
    /** Getter for property viewport.
     * @return Value of property viewport.
     *
     */
    public javax.swing.JViewport getViewport() {
        return viewport;
    }
    
    /** Setter for property viewport.
     * @param viewport New value of property viewport.
     *
     */
    public void setViewport(javax.swing.JViewport viewport) {
        this.viewport = viewport;
        
        this.viewport.setScrollMode(JViewport.BLIT_SCROLL_MODE);
        this.viewport.addChangeListener(new MatrixPanel.ViewPortChangeListener());
        setVisible(true);
    }
    
    /** Getter for property interRowSpace.
     * @return Value of property interRowSpace.
     *
     */
    public int getInterRowSpace() {
        return interRowSpace;
    }
    
    /** Setter for property interRowSpace.
     * @param interRowSpace New value of property interRowSpace.
     *
     */
    public void setInterRowSpace(int interRowSpace) {
        this.interRowSpace = interRowSpace;
        virtualHeight      = (rowResolution+interRowSpace)*matrix.rows();
        visibleRows        = java.lang.Math.min((height-COL_LABEL_WIDTH)/(rowResolution+interRowSpace), matrix.rows());
        vScrollBar.setValues(matrixStartRow, visibleRows, 0, matrix.rows());
        setSize(width, virtualHeight); // For the jscrollpane behind, on simule un e grande taille
        actualSize = new Dimension(width, virtualHeight);
        setPreferredSize(actualSize);
        repaint();
    }
    
    /** Setter for property interColumSpace.
     * @param interColumSpace New value of property interColumSpace.
     *
     */
    public void setInterColumSpace(int interColumSpace) {
        this.interColumSpace = interColumSpace;
        computeCanvasSpread();
        
    }
    
    
    /** Getter for property hScrollBar.
     * @return Value of property hScrollBar.
     *
     */
    public javax.swing.JScrollBar getVScrollBar() {
        return vScrollBar;
    }
    
    /** Setter for property hScrollBar.
     * @param hScrollBar New value of property hScrollBar.
     *
     */
    public void setVScrollBar(javax.swing.JScrollBar vsb) {
        this.vScrollBar = vsb;
        AdjustmentListener [] adls = vScrollBar.getAdjustmentListeners();
        vScrollBar.setValues(matrixStartRow, visibleRows, 0, matrix.rows());
        vScrollBar.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                matrixStartRow = e.getValue();
                if(!e.getValueIsAdjusting())
                    repaint();
            }
        }
        );
    }
    
    /**
     * Getter for property hScrollBar.
     * @return Value of property hScrollBar.
     */
    public javax.swing.JScrollBar getHScrollBar() {
        return hScrollBar;
    }
    
    /**
     * Setter for property hScrollBar.
     * @param hScrollBar New value of property hScrollBar.
     */
    public void setHScrollBar(javax.swing.JScrollBar hScrollBar) {
        this.hScrollBar = hScrollBar;
        AdjustmentListener [] adls = hScrollBar.getAdjustmentListeners();
        hScrollBar.setValues(matrixStartCol, visibleColumns, 0, matrix.columns());
        hScrollBar.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                matrixStartCol = e.getValue();
                if(!e.getValueIsAdjusting())
                    repaint();
            }
        }
        );
        
    }
    
    
    
    
    /** Getter for property backgroundColor.
     * @return Value of property backgroundColor.
     *
     */
    public java.awt.Color getBackgroundColor() {
        return backgroundColor;
    }
    
    /** Setter for property backgroundColor.
     * @param backgroundColor New value of property backgroundColor.
     *
     */
    public void setBackgroundColor(java.awt.Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    
    /**
     * Getter for property matrixCellRenderer.
     * @return Value of property matrixCellRenderer.
     */
    public MatrixCellRenderer getMatrixCellRenderer() {
        return matrixCellRenderer;
    }
    
    /**
     * Setter for property matrixCellRenderer.
     * @param matrixCellRenderer New value of property matrixCellRenderer.
     */
    public void setMatrixCellRenderer(MatrixCellRenderer matrixCellRenderer) {
        this.matrixCellRenderer = matrixCellRenderer;
    }
    
    class ViewPortChangeListener implements ChangeListener {
        // Pour éviter la bave de pixels lors d'un scrolling horizontal
        public void stateChanged(ChangeEvent e) {
            repaint();
        }
        
    }
    
    public void testLU() {
        
        //        LUDecomposition lu = new LUDecomposition(matrix);
        //        System.err.println("Solving...");
        //        int [] p = lu.getPivot();
        //        matrix = matrix.viewSelection(p, p);
        //        if(matrixCellRenderer!=null)
        //            matrixCellRenderer.setMatrix(matrix);
        //
        //        System.err.println("Done");
        //        repaint();
        
        for(int i=0; i<3; i++) {
            matrix = Statistic.correlation(matrix);
            matrix = matrix.viewDice();
        }
        if(matrixCellRenderer!=null)
            matrixCellRenderer.setMatrix(matrix);
        
       // System.err.println("mat="+matrix.toString());
    }
    
    public void EigenvalueDecomposition() {
        //        EigenvalueDecomposition ed = new EigenvalueDecomposition(matrix);
        //        DoubleMatrix2D eigen = ed.getD();
        //
        //
        //            if(matrixCellRenderer!=null)
        //                matrixCellRenderer.setMatrix(matrix);
        //            repaint();
        
        
    }
    
    
    public void sortRows() {
        // EigenvalueDecomposition ed = new EigenvalueDecomposition(matrix);
        // DoubleMatrix2D eigen = ed.getD();
        
        DoubleMatrix1DComparator comp = new DoubleMatrix1DComparator() {
            public int compare(DoubleMatrix1D a, DoubleMatrix1D b) {
                //                double as = a.zSum()/a.size();
                //                double bs = b.zSum()/b.size();
                double as = a.zSum();
                double bs = b.zSum();
                
                return as > bs ? -1 : as == bs ? 0 : 1;
            }
        };
        
        String [] newRowLabels = new String[matrix.rows()];
        int  [] newRowTypes = new int[matrix.rows()];
        matrix = MatrixSorting.sort(matrix,rowLabels,newRowLabels, rowTypes,newRowTypes, comp,0, matrix.rows());
        rowLabels = newRowLabels;
        rowTypes  = newRowTypes;
        
        if(matrixCellRenderer!=null) {
            matrixCellRenderer.setMatrix(matrix);
            matrixCellRenderer.setRowTypes(rowTypes);
        }
        repaint();
        
       // System.err.println("sort rows done");
    }
    
    
    
    
    
    public void sortColumns() {
        // EigenvalueDecomposition ed = new EigenvalueDecomposition(matrix);
        // DoubleMatrix2D eigen = ed.getD();
        
        DoubleMatrix1DComparator comp = new DoubleMatrix1DComparator() {
            public int compare(DoubleMatrix1D a, DoubleMatrix1D b) {
                //                double as = a.zSum()/a.size();
                //                double bs = b.zSum()/b.size();
                double as = a.zSum();
                double bs = b.zSum();
                
                return as > bs ? -1 : as == bs ? 0 : 1;
            }
        };
        
        
        String [] newColLabels = new String[matrix.rows()];
        int  [] newColTypes = new int[matrix.rows()];
        matrix = MatrixSorting.sort(matrix.viewDice(),colLabels,newColLabels, colTypes, newColTypes, comp,0, matrix.columns());
        matrix = matrix.viewDice();
        colLabels = newColLabels;
        colTypes =  newColTypes;
        
        
        if(matrixCellRenderer!=null) {
            matrixCellRenderer.setMatrix(matrix);
            
            matrixCellRenderer.setColumnTypes(colTypes);
        }
        repaint();
    }
    
    
    public void sortRowsAndCols() {
        DoubleMatrix1DComparator comp = new DoubleMatrix1DComparator() {
            public int compare(DoubleMatrix1D a, DoubleMatrix1D b) {
                //                double as = a.zSum()/a.size();
                //                double bs = b.zSum()/b.size();
                double as = a.zSum();
                double bs = b.zSum();
                
                return as > bs ? -1 : as == bs ? 0 : 1;
            }
        };
        
        String [] newColLabels = new String[matrix.columns()];
        String [] newRowLabels = new String[matrix.rows()];
        int  [] newColTypes = new int[matrix.rows()];
        int  [] newRowTypes = new int[matrix.rows()];
        for(int i=0; i<1; i++) {
            matrix = MatrixSorting.sort(matrix,rowLabels,newRowLabels, rowTypes,newRowTypes, comp,0, matrix.rows());
            rowLabels = newRowLabels;
            rowTypes  = newRowTypes;
            matrix = MatrixSorting.sort(matrix.viewDice(),colLabels,newColLabels, colTypes, newColTypes,comp,0, matrix.columns());
            matrix = matrix.viewDice();
            colLabels = newColLabels;
            colTypes =  newColTypes;
        }
        if(matrixCellRenderer!=null) {
            matrixCellRenderer.setMatrix(matrix);
            matrixCellRenderer.setRowTypes(rowTypes);
            matrixCellRenderer.setColumnTypes(colTypes);
        }
        repaint();
        
        
    }
    
    
    /**
     * Getter for property paintHVLines.
     * @return Value of property paintHVLines.
     */
    public boolean isPaintHVLines() {
        return paintHVLines;
    }
    
    /**
     * Setter for property paintHVLines.
     * @param paintHVLines New value of property paintHVLines.
     */
    public void setPaintHVLines(boolean paintHVLines) {
        this.paintHVLines = paintHVLines;
    }
    
    /**
     * Getter for property paintValues.
     * @return Value of property paintValues.
     */
    public boolean isPaintValues() {
        return paintValues;
    }
    
    /**
     * Setter for property paintValues.
     * @param paintValues New value of property paintValues.
     */
    public void setPaintValues(boolean paintValues) {
        this.paintValues = paintValues;
    }
    
    /**
     * Getter for property paintCross.
     * @return Value of property paintCross.
     */
    public boolean isPaintCross() {
        return paintCross;
    }
    
    /**
     * Setter for property paintCross.
     * @param paintCross New value of property paintCross.
     */
    public void setPaintCross(boolean paintCross) {
        this.paintCross = paintCross;
    }
    
    /**
     * Getter for property paintLabels.
     * @return Value of property paintLabels.
     */
    public boolean isPaintLabels() {
        return paintLabels;
    }
    
    /**
     * Setter for property paintLabels.
     * @param paintLabels New value of property paintLabels.
     */
    public void setPaintLabels(boolean paintLabels) {
        this.paintLabels = paintLabels;
    }
    
    public void mouseDragged(java.awt.event.MouseEvent mouseEvent) {
    }
    
    public void mouseMoved(java.awt.event.MouseEvent mouseEvent) {
        int x = mouseEvent.getX();
        int y = mouseEvent.getY();
        
        // Déterminer la ligne et la colonne correspondante dans la matrice de donnée
        if(paintLabels) {
            x -= COL_LABEL_WIDTH;
            y -= ROW_LABEL_WIDTH;
        }
        
        currentMouseCol = x/(interColumSpace+colResolution);
        currentMouseRow = y/(interRowSpace+rowResolution);
        
        if(currentMouseCol<0) currentMouseCol = 0;
        if(currentMouseRow<0) currentMouseRow = 0;
        
        if(currentMouseRow>(matrix.columns()-1)) currentMouseRow = matrix.columns()-1;
        if(currentMouseCol>(matrix.rows()-1))    currentMouseCol = matrix.rows()-1;
        
        if(paintCross) this.repaint();
    }
    
    /**
     * Getter for property rowLabels.
     * @return Value of property rowLabels.
     */
    public java.lang.String[] getRowLabels() {
        return this.rowLabels;
    }
    
    /**
     * Setter for property rowLabels.
     * @param rowLabels New value of property rowLabels.
     */
    public void setRowLabels(java.lang.String[] rowLabels) {
        this.rowLabels = rowLabels;
    }
    
    /**
     * Getter for property colLabels.
     * @return Value of property colLabels.
     */
    public java.lang.String[] getColLabels() {
        return this.colLabels;
    }
    
    /**
     * Setter for property colLabels.
     * @param colLabels New value of property colLabels.
     */
    public void setColLabels(java.lang.String[] colLabels) {
        this.colLabels = colLabels;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        
        final char c = e.getActionCommand().charAt(0);
        
        if(c=='r') {
            sortRows();
        } else
            if(c=='c') {
                sortColumns();
            }
            else
                if(c=='&') {
                    sortRowsAndCols();
                }
        
    }
    
    /**
     * Getter for property rowTypes.
     * @return Value of property rowTypes.
     */
    public int[] getRowTypes() {
        return this.rowTypes;
    }
    
    /**
     * Setter for property rowTypes.
     * @param rowTypes New value of property rowTypes.
     */
    public void setRowTypes(int[] rowTypes) {
        this.rowTypes = rowTypes;
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
    
    class PopupListener extends MouseAdapter {
        @Override
		public void mousePressed(MouseEvent e   ) {
            maybeShowPopup(e);
        }
        @Override
		public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
        private void maybeShowPopup(final MouseEvent e) {
            if (e.isPopupTrigger()) {
                
                final Point p   = new Point(e.getX(), e.getY());
                
                popupMenu.show(e.getComponent(),p.x,p.y);
                
            }
            
            
        }
    }
    
}
