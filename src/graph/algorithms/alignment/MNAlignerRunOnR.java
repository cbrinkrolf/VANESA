package graph.algorithms.alignment;

import java.io.File;
import java.io.IOException;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.jet.math.Functions;

public class MNAlignerRunOnR extends GraphAlignmentAlgorithms {

	private DoubleMatrix2D Q, A;
	private double[] b, c;
	private double lambda;
	private int dimA, dimB;
	private RConnection con;

	private DoubleMatrix2D similarityMatrix, reducedSimilarityMatrix;
	
	private cern.jet.math.Functions F = cern.jet.math.Functions.functions;

	public MNAlignerRunOnR(AdjacencyMatrix graphA, AdjacencyMatrix graphB,
			SimilarityMatrix simMatrix, double lambda, RConnection c) {

		this.similarity = simMatrix;
		this.lambda = lambda;
		this.graphA = graphA;
		this.graphB = graphB;
		this.similarityMatrix = simMatrix.getMatrix().copy();
		this.con = c;
		
		dimA = graphA.getMatrix().rows();
		dimB = graphB.getMatrix().rows();
		double sub = similarityMatrix.zSum() / (dimA * dimB);
		this.reducedSimilarityMatrix = similarityMatrix.copy();
		this.reducedSimilarityMatrix.assign(Functions.minus(sub));

	}

	@Override
	public void run() throws Exception {
//		System.out.println("Building model");
		initMNAligner();
//		buildQuadraticProgrammingModel();
//		System.out.println("Model building finished");
//		System.out.println("Calling R script");
		callMNAlignerRScript();
	}

	private void initMNAligner() throws RserveException {
		
		con.voidEval("DimA <- c(" + dimA + ")");
		con.voidEval("DimB <- c(" + dimB + ")");
//		System.out.println("Dim graph A: " + dimA);
//		System.out.println("Dim graph B: " + dimB);
		
//		System.out.println("build matrix cc");
		con.voidEval("cc <- matrix(0, " + ((dimA*dimB)+dimA+dimB) + ", 1)");
		for (int i = 1; i <= dimA; i++) {
			for (int j = 1; j <= dimB; j++) {
				double val = lambda * reducedSimilarityMatrix.get(i - 1, j - 1);
				// c.set(((i-1)*dimB +j)-1, 0, val);
//				c[((i - 1) * dimB + j) - 1] = -val;
				con.voidEval("cc[" + (((i-1)*dimB)+j) + ", 1] <- " + (-val));
			}
		}
//		System.out.println("matrix cc finished");
		
//		System.out.println("build matrix A & bb");
		con.voidEval("A <- matrix(0.0, " + (dimA+dimB) + ", " + (dimA*dimB+dimA+dimB) + ")");
		con.voidEval("bb <- matrix(0.0, " + (dimA+dimB) + ", " + (1) + ")");
		for (int i = 1; i <= dimA; i++) {
			for (int j = 1; j <= dimB; j++) {
//				A.set(i - 1, ((i - 1) * dimB + j) - 1, 1);
				con.voidEval("A[" + i + ", " + ((i-1)*dimB+j) + "] <- " + 1);
			}
		}
		for (int i = 1; i <= dimB; i++) {
			for (int j = 1; j <= dimA; j++) {
				con.voidEval("A[" + (dimA+i) + ", " + ((j-1)*dimB+i) + "] <- " + 1);
			}
		}
		for (int i = 1; i <= dimA + dimB; i++) {
			con.voidEval("A[" + (i) + ", " + (dimA*dimB+i) + "] <- " + 1);
			// b.set(i-1, 0, 1);
//			b[i - 1] = 1;
			con.voidEval("bb[" + (i) + ", " + (1) + "] <- " + 1);
		}
//		System.out.println("matrix A & bb finished");
		
//		System.out.println("build matrix Q");
		Q = new SparseDoubleMatrix2D(dimA * dimB + dimA + dimB, dimA * dimB
				+ dimA + dimB);
		con.voidEval("Q <- matrix(0.0, " + (dimA*dimB+dimA+dimB) + ", " + (dimA*dimB+dimA+dimB) + ")");
		for (int i = 1; i <= dimA; i++) {
			for (int j = 1; j <= dimB; j++) {
				for (int k = 1; k <= dimA; k++) {
					for (int l = 1; l <= dimB; l++) {
						double val = (1 - lambda)
								* (graphA.getMatrix().get(i - 1, k - 1)
										* graphB.getMatrix().get(j - 1, l - 1) + graphA
										.getMatrix().get(k - 1, i - 1)
										* graphB.getMatrix().get(l - 1, j - 1));
						Q.set((i - 1) * dimB + j - 1, (k - 1) * dimB + l - 1,
								-val);
//						con.voidEval("Q[" + ((i-1)*dimB+j) + ", " + ((k-1)*dimB+l) + "] <- " + -val);
					}
				}
			}
		}
		IntArrayList rowList, colList;
		rowList = new IntArrayList();
		colList = new IntArrayList();
		DoubleArrayList valList = new DoubleArrayList();
		Q.getNonZeros(rowList, colList, valList);
//		con.voidEval("Q <- matrix(0.0, nrow = " + Q.rows() + ", ncol = " + Q.columns() + ")");
		try {
			con.assign("qRow", rowList.elements());
			con.assign("qCol", colList.elements());
			con.assign("qVal", valList.elements());
		} catch (REngineException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String path = (new File("")).getAbsolutePath();
		if(System.getProperty("os.name").startsWith("Windows")){
			path = path.replace('\\', '/');
		}
//		System.out.println("Start R init-script");
		path += "/src/graph/algorithms/alignment/initMNAligner.r";
		con.voidEval(" source(\"" + path + "\") ");
//		System.out.println("R init-script finished");
//		System.out.println("matrix Q finished");
		
		
		// TEST //
//		REXP test = con.eval("Q");
//		try {
//			System.out.println( new SparseDoubleMatrix2D(test.asDoubleMatrix()));
//		} catch (REXPMismatchException e) {
//			e.printStackTrace();
//		}
		
	}

	private void buildQuadraticProgrammingModel() throws Exception{

		dimA = graphA.getMatrix().rows();
		dimB = graphB.getMatrix().rows();
		cern.jet.math.Functions F = cern.jet.math.Functions.functions;

		double sub = similarityMatrix.zSum() / (dimA * dimB);
		// DoubleMatrix2D reducedSimilarityMatrix = S.copy();
		similarityMatrix.assign(Functions.minus(sub));
//		System.err.println(dimA*dimB+dimA+dimB);
		// Initialize matrices
		// b = new DenseDoubleMatrix2D(dimA + dimB, 1);
		b = new double[dimA + dimB];
		// c = new DenseDoubleMatrix2D(dimA*dimB+dimA+dimB, 1);
		c = new double[dimA * dimB + dimA + dimB];
		
//		try {
//			Q = new DenseDoubleMatrix2D(dimA * dimB + dimA + dimB, dimA * dimB
//					+ dimA + dimB);
//		} catch (Exception e) {
//			e.printStackTrace();
			Q = new SparseDoubleMatrix2D(dimA * dimB + dimA + dimB, dimA * dimB
					+ dimA + dimB);
//			q = new double[(dimA*dimB+dimA+dimB) * (dimA*dimB+dimA+dimB)];
//		}
//		try {
//			A = new DenseDoubleMatrix2D(dimA + dimB, dimA * dimB + dimA + dimB);
//		} catch (Exception e) {
//			e.printStackTrace();
			A = new SparseDoubleMatrix2D(dimA + dimB, dimA * dimB + dimA + dimB);
//		}
		
		// fill matrices
		for (int i = 1; i <= dimA; i++) {
			for (int j = 1; j <= dimB; j++) {
				double val = lambda * similarityMatrix.get(i - 1, j - 1);
				// c.set(((i-1)*dimB +j)-1, 0, val);
				c[((i - 1) * dimB + j) - 1] = -val;
			}
		}

		for (int i = 1; i <= dimA; i++) {
			for (int j = 1; j <= dimB; j++) {
				A.set(i - 1, ((i - 1) * dimB + j) - 1, 1);
			}
		}
		for (int i = 1; i <= dimB; i++) {
			for (int j = 1; j <= dimA; j++) {
				A.set(dimA + i - 1, (j - 1) * dimB + i - 1, 1);
			}
		}
		for (int i = 1; i <= dimA + dimB; i++) {
			A.set(i - 1, dimA * dimB + i - 1, 1);
			// b.set(i-1, 0, 1);
			b[i - 1] = 1;
		}
		// System.out.println(b);
		// System.out.println(A);

		for (int i = 1; i <= dimA; i++) {
			for (int j = 1; j <= dimB; j++) {
				for (int k = 1; k <= dimA; k++) {
					for (int l = 1; l <= dimB; l++) {
						double val = (1 - lambda)
								* (graphA.getMatrix().get(i - 1, k - 1)
										* graphB.getMatrix().get(j - 1, l - 1) + graphA
										.getMatrix().get(k - 1, i - 1)
										* graphB.getMatrix().get(l - 1, j - 1));
						Q.set((i - 1) * dimB + j - 1, (k - 1) * dimB + l - 1,
								val);
					}
				}
			}
		}
		Q.assign(Functions.neg);
		// c.assign(F.neg);
		// System.out.println(Q);
		// System.out.println(c);

	}

	private void callMNAlignerRScript() throws Exception {

//		System.out.println("Prepare matrices");
//		
//		// System.out.println(A);
//		double[] a = new double[A.rows() * A.columns()];
//		int counter = 0;
//		for (int i = 0; i < A.columns(); i++) {
//			for (int j = 0; j < A.rows(); j++) {
//				a[counter] = A.get(j, i);
//				counter++;
//			}
//		}
//		
//		System.out.println("A finished");

		// System.out.println(Q);
//		double[] q = new double[Q.rows() * Q.columns()];
//		counter = 0;
//		for (int i = 0; i < Q.columns(); i++) {
//			for (int j = 0; j < Q.rows(); j++) {
//				q[counter] = Q.get(j, i);
//				counter++;
//			}
//		}
//		double[] q = Q.like1D(Q.rows()*Q.columns()).toArray();
		
//		IntArrayList rowList, colList;
//		rowList = new IntArrayList();
//		colList = new IntArrayList();
//		DoubleArrayList valList = new DoubleArrayList();
//		Q.getNonZeros(rowList, colList, valList);
//		System.out.println("Q finished");

		// // System.out.println(S);
		// double[] s = new double[S.rows()*S.columns()];
		// counter = 0;
		// for (int i = 0; i < S.columns(); i++) {
		// for (int j = 0; j < S.rows(); j++) {
		// s[counter] = S.get(j, i);
		// counter++;
		// }
		// }

		// System.out.println("result="+StartRserve.checkLocalRserve());

//		con.voidEval("rm(list = ls())");

		// con.assign("s", s);
		// con.voidEval("S<-matrix(s,"+S.rows()+","+S.columns()+")");
		// // DoubleMatrix2D NSS = new
		// DenseDoubleMatrix2D(con.eval("S").asDoubleMatrix());
		// // System.out.println(NSS);

//		con.voidEval("DimA <- c(" + dimA + ")");
//		con.voidEval("DimB <- c(" + dimB + ")");

//		con.assign("qtmp", q);
//		con.voidEval("Q <- matrix(qtmp, " + Q.rows() + ", " + Q.columns() + ")");
		
//		con.voidEval("Q <- matrix(0.0, nrow = " + Q.rows() + ", ncol = " + Q.columns() + ")");
//		con.assign("qRow", rowList.elements());
//		con.assign("qCol", colList.elements());
//		con.assign("qVal", valList.elements());
		
		// DoubleMatrix2D QQ = new
		// DenseDoubleMatrix2D(con.eval("Q").asDoubleMatrix());
		// System.out.println(QQ);

//		con.assign("atmp", a);
//		con.voidEval("A<-matrix(atmp," + A.rows() + "," + A.columns() + ")");
		// DoubleMatrix2D AA = new
		// DenseDoubleMatrix2D(con.eval("A").asDoubleMatrix());
		// System.out.println(AA);

//		con.assign("btmp", b);
//		con.voidEval("bb<-matrix(btmp," + b.length + "," + 1 + ")");
		// DoubleMatrix2D bb = new
		// DenseDoubleMatrix2D(con.eval("b").asDoubleMatrix());
		// System.out.println(bb);

//		con.assign("ctmp", c);
//		con.voidEval("cc<-matrix(ctmp," + c.length + "," + 1 + ")");
		// DoubleMatrix2D cc = new
		// DenseDoubleMatrix2D(con.eval("c").asDoubleMatrix());
		// System.out.println(cc);

		con.voidEval("Lambda <- " + lambda);

		// a = q = s = null;
		// A = Q = null;
		// System.gc();

		
		String path = (new File("")).getAbsolutePath();
		if(System.getProperty("os.name").startsWith("Windows")){
			path = path.replace('\\', '/');
		}
		
//		System.out.println("Start R script");
		path += "/src/graph/algorithms/alignment/MNAligner.r";
//		path += "/src/graph/algorithms/alignment/initMNAligner.r";
		
		con.voidEval(" source(\"" + path + "\") ");
//		System.out.println("R script finished");

		REXP test = con.eval("solution");
		solutionMatrix = new DenseDoubleMatrix2D(test.asDoubleMatrix());
//		System.out.println(solutionMatrix);
		
//		REXP test = con.eval("Q");
//		solutionMatrix = new SparseDoubleMatrix2D(test.asDoubleMatrix());
//		System.out.println(solutionMatrix);
//		System.out.println("");
//		System.out.println(Q);
//		System.out.println(rowList);
//		System.out.println(colList);
//		System.out.println(valList);
		
		// System.out.println(solution);
		// System.out.println(test.asDouble());

//		 double[][] test = con.parseAndEval("Q").asDoubleMatrix();
//		 DoubleMatrix2D matrix = new DenseDoubleMatrix2D(test);
//		 System.out.println(matrix);
		// System.out.println(c.eval("DimA").asInteger());

		 con.close();
		// c.shutdown();

	}

	public static void main(String[] args) {

		double[][] vals1 = { { 0, 1, 1, 1 }, { 0, 0, 1, 0 }, { 1, 0, 0, 0 },
				{ 0, 1, 0, 0 } };
		DoubleMatrix2D NA = new DenseDoubleMatrix2D(vals1);

		double[][] vals2 = { { 0, 1, 0 }, { 0, 0, 1 }, { 1, 0, 0 }, };
		DoubleMatrix2D NB = new DenseDoubleMatrix2D(vals2);

		double[][] vals3 = { { 1, .1, 0 }, { 0, .9, .1 }, { .6, 0, .8 },
				{ 0, .1, 0 } };
		DoubleMatrix2D S = new DenseDoubleMatrix2D(vals3);

		DoubleMatrix2D Q = new DenseDoubleMatrix2D(2000,2000);
		
		
		
		File l = new File(".");
		try {
			System.out.println(l.getCanonicalPath()+"src/graph/algorithms/alignment/MNAligner.r");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		String path = l.getAbsolutePath().replace('\\', '/');
//		System.out.println(System.getProperty("os.name").startsWith("Windows"));
//		try {
//			System.out.println(path);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// lambda = 0.5;
		//
		// // int n, m;
		// // n = 10;
		// // m = 150;
		// // DoubleFactory2D F = DoubleFactory2D.dense;
		// // NA = F.random(n, n);
		// // NB = F.random(m, m);
		// // NSS = F.random(n, m);
		//
		// long time = System.currentTimeMillis();
		//
		// buildQuadraticProgrammingModel(NA, NB, S);
		//
		// DoubleMatrix2D result = callMNAlignerRScript();
		//
		// time = System.currentTimeMillis() - time;
		// System.out.println("Elapsed time is " + time + " millisecondes");
		//
		// System.out.println(result);

	}

}
