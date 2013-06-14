/*
 * Test.java
 *
 * Created on 9 mars 2004, 21:05
 */

package gui.jmatrixview;
import javax.swing.JFrame;

import cern.colt.function.DoubleFunction;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;

//import agile2d.AgileJFrame;
/**
 * 
 * @author  semiosys
 */
public class Test {
    
    static boolean  opengl = true;
    static java.util.Random random = new java.util.Random();
    /** Creates a new instance of Test */
    public Test() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int size = 30;
        DoubleFactory2D factory = DoubleFactory2D.sparse;
        DoubleMatrix2D matrix = factory.make(size,size);
        
//        for(int i=0; i<size; i++) {
//            for(int j=i+1; j<size; j++) {
//                int v = random.nextInt(10);
//              
//                    matrix.setQuick(i, j, v);
//            }
//        }
        
                matrix.assign(new DoubleFunction() {
                    //MersenneTwister MT = new cern.jet.random.engine.MersenneTwister(1);
                    private int i = 0;
        
                    public double apply(double argument) {
                        int v = random.nextInt(3);
                        i++;
                        if(v==0) return 0;
                        return random.nextInt(8);
                    }
                });
        
        
        String [] rowLabels = new String[size];
        int [] rowTypes = new int[size];
        int [] colTypes = new int[size];
        
        int t = 0;
        for(int l=0; l<size; l++) {
            rowLabels[l] = "node "+ String.valueOf(l);
            
            rowTypes[l] = random.nextInt(4);
            colTypes[l] = rowTypes[l];
        }
        
        
        double minmin, maxmax;
        minmin = Double.MAX_VALUE;
        maxmax = Double.MIN_VALUE;
        for(int i=0; i<matrix.columns(); i++) {
            DoubleMatrix1D matcol = matrix.viewColumn(i);
            double min = matcol.aggregate(Functions.min, Functions.identity);
            double max = matcol.aggregate(Functions.max, Functions.identity);
            minmin = Math.min(min, minmin);
            maxmax = Math.max(max, maxmax);
            
        }
        minmin=0;
        
        JFrame f = null;
        // if(opengl)
        // if = new  AgileJFrame("MatViz / openGL");
        // else
        f= new JFrame("Semiosys JMatrixView");
        
        f.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
                System.exit(0);
            }
        });
        f.setSize(700, 700);
        MultiShapeMatrixCellRenderer dmcr = new MultiShapeMatrixCellRenderer( 
        matrix, 
        java.awt.Color.white, 
        java.awt.Color.orange,
        minmin, 
        maxmax,
        rowTypes,
        colTypes);
        MatrixControlPanel mcp = new MatrixControlPanel( 700, 700);
        
        f.getContentPane().add(mcp);
        mcp.setData(matrix, dmcr, rowLabels , rowLabels, rowTypes, colTypes); 
        f.setVisible(true);
        
    }
    // adelaide281@hotmail.com
}

