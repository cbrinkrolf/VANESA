package petriNet;

import java.util.ArrayList;

import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.LUDecomposition;
import cern.colt.matrix.linalg.QRDecomposition;
import cern.colt.matrix.linalg.SingularValueDecomposition;

public class Test {

	public Test(){
		
		
	}
	
	private static void test(){
		double[][] m1d = new double[3][3];
		double[][] m2d = new double[5][1];
		
		m1d[0][0] = -2;
		m1d[0][1] = 2;
		m1d[0][2] = -1;
		m1d[1][0] = 2;
		m1d[1][1] = -3;
		m1d[1][2] = 1;
		m1d[2][0] = 2;
		m1d[2][1] = -1;
		m1d[2][2] = 5;
		
		m2d[0][0] = 0;
		m2d[1][0] = 0;
		m2d[2][0] = 0;
		m2d[3][0] = 0;
		m2d[4][0] = 0;
		
		double[][] fd = new double[5][4];
		double[][] bd = new double[5][4];
		
		
		fd[0][0] = 1;
		fd[0][1] = 0;
		fd[0][2] = 0;
		fd[0][3] = 0;
		
		fd[1][0] = 1;
		fd[1][1] = 0;
		fd[1][2] = 0;
		fd[1][3] = 0;
		
		fd[2][0] = 0;
		fd[2][1] = 2;
		fd[2][2] = 0;
		fd[2][3] = 0;
		
		fd[3][0] = 0;
		fd[3][1] = 0;
		fd[3][2] = 0;
		fd[3][3] = 1;
		
		fd[4][0] = 0;
		fd[4][1] = 0;
		fd[4][2] = 1;
		fd[4][3] = 0;
		//b
		bd[0][0] = 0;
		bd[0][1] = 1;
		bd[0][2] = 0;
		bd[0][3] = 0;
		
		bd[1][0] = 0;
		bd[1][1] = 1;
		bd[1][2] = 0;
		bd[1][3] = 0;
		
		bd[2][0] = 0;
		bd[2][1] = 0;
		bd[2][2] = 1;
		bd[2][3] = 0;
		
		bd[3][0] = 1;
		bd[3][1] = 1;
		bd[3][2] = 0;
		bd[3][3] = 0;
		
		bd[4][0] = 2;
		bd[4][1] = 0;
		bd[4][2] = 0;
		bd[4][3] = 0;


		double[][] cd = new double[5][5];
		cd[0][0] = -1;
		cd[0][1] = 1;
		cd[0][2] = 0;
		cd[0][3] = 0;
		cd[0][4] = 0;
		
		cd[1][0] = -1;
		cd[1][1] = 1;
		cd[1][2] = 0;
		cd[1][3] = 0;
		cd[1][4] = 0;
		
		cd[2][0] = 0;
		cd[2][1] = -2;
		cd[2][2] = 1;
		cd[2][3] = 0;
		cd[2][4] = 0;
		
		cd[3][0] = 1;
		cd[3][1] = 1;
		cd[3][2] = 0;
		cd[3][3] = -1;
		cd[3][4] = 0;
		
		cd[4][0] = 2;
		cd[4][1] = 0;
		cd[4][2] = -1;
		cd[4][3] = 0;
		cd[4][4] = 0;
		
		
		
		DenseDoubleMatrix2D m1 = new DenseDoubleMatrix2D(m1d);
		DenseDoubleMatrix2D m2 = new DenseDoubleMatrix2D(m2d);
		
		DenseDoubleMatrix2D f = new DenseDoubleMatrix2D(fd);
		DenseDoubleMatrix2D b = new DenseDoubleMatrix2D(bd);
		DenseDoubleMatrix2D c = new DenseDoubleMatrix2D(cd);
		
		
		Algebra a = new Algebra();
		//DoubleMatrix2D m3 = a.solve(usv, m2);
		//System.out.println(m3);
		double[] vd = new double[5];
		vd[0] = 1;
		vd[1] = 1;
		vd[2] = 2;
		vd[3] = 2;
		vd[4] = 2;
		
		DenseDoubleMatrix1D v = new DenseDoubleMatrix1D(vd);
		DenseDoubleMatrix1D x = new DenseDoubleMatrix1D(5);
		c.zMult(v, x, 1,0, false);
		//System.out.println(x);
		IntArrayList l = new IntArrayList();
		x.getNonZeros(l, null);
		//System.out.println(l.size());
		
		
	}
	
	public static void main(String[] args){
		test();
	}
}
