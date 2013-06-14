package graph.algorithms.alignment;

import java.util.HashMap;

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;
import cern.jet.random.Uniform;

public class MNAligner extends GraphAlignmentAlgorithms{

	private DoubleMatrix2D similarityMatrix, reducedSimilarityMatrix;
	
	private DoubleMatrix2D Q , A;
	private DoubleMatrix2D b, c;
	private int dimA, dimB;
	private double lambda;
	private double tolerance, beta, alpha, nora, obvalue;
	
	private DoubleArrayList obhis, zhis;
	
	private Algebra algebra = new Algebra();
	private cern.jet.math.Functions F = cern.jet.math.Functions.functions;
	
	private int m, n;
	private DoubleMatrix2D a, x, comp;
	private DoubleMatrix2D y;
	private double ob, z, lamda, gap;
	
	private DoubleFactory2D factory2d;
	private DoubleFactory1D factory1d;
	
	private final double EPS = Math.pow(2, -52);
	
	public MNAligner(AdjacencyMatrix a, AdjacencyMatrix b, DoubleMatrix2D sim, double lambda){
		
		this.graphA = a;
		this.graphB = b;
		this.similarityMatrix = sim;
		
		this.lambda = lambda;
		this.tolerance = 1.e-5;
		this.beta = 0.8;
		this.alpha = 0.95;

		this.dimA = graphA.getMatrix().columns();
		this.dimB = graphB.getMatrix().columns();
		
	}
	
	@Override
	public void run(){
		
		double sub = similarityMatrix.zSum() / (dimA * dimB);
		this.reducedSimilarityMatrix = similarityMatrix.copy();
		this.reducedSimilarityMatrix.assign(Functions.minus(sub));
		
//		System.out.println("Build Model");
		this.buildQuadraticProgrammingModel();
//		System.out.println("Build Alignment");
		this.solveQuadraticProgramBySPSOLQP();
		
//		solutionMatrix = factory2d.make(x.viewPart(0, dimB*dimA).toArray(), dimB);
//		System.out.println(getSolutionMatrix());
		
	}
	
	private void buildQuadraticProgrammingModel(){
		
		// Initialize matrices
		b = new DenseDoubleMatrix2D(dimA + dimB, 1);
		c = new DenseDoubleMatrix2D(dimA*dimB+dimA+dimB, 1);
		try {
			Q = new DenseDoubleMatrix2D(dimA*dimB+dimA+dimB, dimA*dimB+dimA+dimB);
		} catch (Exception e) {
			e.printStackTrace();
			Q = new SparseDoubleMatrix2D(dimA*dimB+dimA+dimB, dimA*dimB+dimA+dimB);
		}
		try {
			A = new DenseDoubleMatrix2D(dimA+dimB, dimA*dimB+dimA+dimB);
		} catch (Exception e) {
			e.printStackTrace();
			A = new SparseDoubleMatrix2D(dimA+dimB, dimA*dimB+dimA+dimB);
		}
		
		// fill matrices
		for (int i = 1; i <= dimA; i++) {
			for (int j = 1; j <= dimB; j++) {
				double val = lambda*reducedSimilarityMatrix.get(i-1, j-1);
				c.set(((i-1)*dimB +j)-1, 0, val);
			}
		}
		
		for (int i = 1; i <= dimA ; i++) {
			for (int j = 1; j <= dimB; j++) {
				A.set(i-1, ((i-1)*dimB+j)-1, 1);
			}
		}
		for (int i = 1; i <= dimB ; i++) {
			for (int j = 1; j <= dimA; j++) {
				A.set(dimA+i-1, (j-1)*dimB+i-1, 1);
			}
		}
		for (int i = 1; i <= dimA+dimB; i++) {
			A.set(i-1, dimA*dimB+i-1, 1);
			b.set(i-1, 0, 1);
		}
//		System.out.println(b);
//		System.out.println(A);
		
		for (int i = 1; i <= dimA; i++) {
			for (int j = 1; j <= dimB; j++) {
				for (int k = 1; k <= dimA; k++) {
					for (int l = 1; l <= dimB; l++) {
//						double val = (1-lambda) * (graphA.getMatrix().get(i-1, k-1) * graphB.getMatrix().get(j-1, l-1)
//										+ graphA.getMatrix().get(k-1, i-1) * graphB.getMatrix().get(l-1, j-1));
						double val = (1-lambda) * (graphA.getMatrix().get(i-1, k-1) * graphB.getMatrix().get(j-1, l-1)
								+ graphA.getMatrix().get(k-1, i-1) * graphB.getMatrix().get(l-1, j-1));
						val += Q.get((i-1)*dimB+j-1, (k-1)*dimB+l-1);
						Q.set((i-1)*dimB+j-1, (k-1)*dimB+l-1, val);
					}
				}
			}
		}
		Q.assign(Functions.neg);
		c.assign(Functions.neg);
//		System.out.println(Q);
//		System.out.println(c);
	}
	
	
	private void solveQuadraticProgramBySPSOLQP(){
		
		m = A.rows();
		n = A.columns();
		
		DoubleMatrix2D ones = new DenseDoubleMatrix2D(n, 1).assign(1);
		a = b.copy().assign(algebra.mult(A, ones), Functions.minus);
		x = new DenseDoubleMatrix2D(n+1, 1).assign(1);
		z = 0;
		ob = x.get(n, 0);
		obhis = new DoubleArrayList();
		obhis.add(ob);
		gap = ob - z;
		
		// Phase 1
		//System.out.println("Start phase 1");
		while (gap >= tolerance) {
			callSPPphase1();
			ob = x.get(n, 0);
			obhis.add(ob);
			gap = ob - z;
			if (z > 0) {
				gap = -1;
			//	System.out.println("No feasible solution");
				return;
			}
		}
//		System.out.println(x);

		// Phase 2
	//	System.out.println("Start phase 2");
		alpha = 0.9;
		x = x.viewPart(0, 0, n, 1);
		
		DoubleArrayList list = new DoubleArrayList();
//		double start = 0.01;
		for (int i = 0; i < n; i++) {
			list.add(Uniform.staticNextDouble());
//			list.add(start);
//			start += 0.03;
		}
		
		factory2d = DoubleFactory2D.dense;
		list.trimToSize();
		comp = factory2d.make(list.elements(), 1);
		comp = algebra.transpose(comp);
		
		factory2d = DoubleFactory2D.sparse;
		DoubleMatrix2D speye = factory2d.identity(n);
		DoubleMatrix2D[][] parts = {
				{speye, algebra.transpose(A)},
				{A, factory2d.make(m, m)}
		};
		DoubleMatrix2D ans1 = factory2d.compose(parts);
		DoubleMatrix2D ans2 = factory2d.appendRows(comp, factory2d.make(m, 1));
		DoubleMatrix2D ans = algebra.solve(ans1, ans2);
		
		comp = ans.viewPart(0, 0, n, 1);
		ans = ans1 = ans2 = null;
		
		DoubleMatrix2D tmp = comp.copy().assign(x, Functions.div);
		nora = tmp.aggregate(Functions.min, Functions.identity);
//		System.out.println(nora);
		
		if(nora < 0)
			nora = -0.01/nora;
		else{
			nora = tmp.aggregate(Functions.max, Functions.identity);
			if(nora == 0){
		//		System.out.println("The problem has a unique feasible point");
				return;
			}
			nora = 0.01/nora;
		}
		
		x.assign(comp, Functions.plusMult(nora));
		
//		obvalue = x.zDotProduct(algebra.mult(Q, x)) / 2 + c.zDotProduct(x);
		obvalue = algebra.mult(algebra.transpose(x), algebra.mult(Q, x)).get(0, 0);
		obvalue = obvalue / 2 + algebra.mult(algebra.transpose(c), x).get(0, 0);
		
		obhis.clear();
		obhis.add(obvalue);
		
		double lower = Double.NEGATIVE_INFINITY;
		zhis = new DoubleArrayList();
		zhis.add(lower);
		
		gap = 1;
		lamda = Math.max(1, Math.abs(obvalue)/Math.sqrt(Math.sqrt(n)) );
//		System.out.println(lambda);
		
		int iter = 0;
		while (gap >= tolerance) {
			iter++;
			callSPPphase2();
			if (ob == Double.NEGATIVE_INFINITY) {
				gap = 0;
		//		System.out.println("The problem is unbounded");
				return;
			} else {
				obhis.add(ob);
				comp = algebra.mult(Q, x).assign(c, Functions.plus);
				comp = comp.assign(algebra.mult(algebra.transpose(A), y), Functions.minus);
//				System.out.println(comp);
//				DoubleMatrix1D tmp1 = algebra.mult(Q, x).assign(c, F.plus);
//				comp = tmp1.assign(algebra.mult(algebra.transpose(A), y).viewColumn(0), F.minus);
////				System.out.println(comp);
				
				if (comp.aggregate(Functions.min, Functions.identity) >= 0) {
					lower = ob - algebra.mult(algebra.transpose(x), comp).get(0, 0);
					zhis.add(lower);
					gap = (ob-lower) / (1+Math.abs(ob));
					obvalue = ob;
//					System.out.println("HIER 1: ");
				} else {
					lower = zhis.get(iter-1);
					zhis.add(lower);
					gap = (obvalue-ob) / (1 + Math.abs(ob));
					obvalue = ob;
//					System.out.println("HIER 2 :" + lower);
				}
			}
		}
	//	System.out.println("A (local) optimal solution is found");
	//	System.out.println("Objectiv function value: f*= " + obhis.get(obhis.size()-1));
	}
	
	
	
	
	private void callSPPphase2() {
		
		lamda = (1.0 - beta)*lamda;
		double go = 0;
		DoubleMatrix2D gg = algebra.mult(Q, x).assign(c, Functions.plus);
//		DoubleMatrix2D gg = factory2d.make(gg1d.toArray(), 1);
//		System.out.println(gg);
		
		factory2d = DoubleFactory2D.sparse;
		DoubleMatrix2D XX = factory2d.diagonal(x.viewColumn(0));
		DoubleMatrix2D AA = algebra.mult(A, XX);
		XX = algebra.mult(algebra.mult(XX, Q), XX);
//		System.out.println(XX);
		
		factory2d = DoubleFactory2D.sparse;
		DoubleMatrix2D speye, ans1, ans2, xx;
		DoubleMatrix2D u = null;
		
		while (go <= 0) {
			speye = factory2d.identity(n);
			ans1 = XX.copy().assign(speye, Functions.plusMult(lamda));
			DoubleMatrix2D[][] parts = {
					{ans1, algebra.transpose(AA)},
					{AA, factory2d.make(m, m)}
			};
			ans1 = factory2d.compose(parts);
//			System.out.println(ans1);
			ans2 = x.copy().assign(Functions.neg);
			ans2 = gg.copy().assign(ans2, Functions.mult);
			ans2 = factory2d.appendRows(ans2, factory2d.make(m, 1));
//			System.out.println(ans1);
			
			u = algebra.solve(ans1, ans2);
			
//			DoubleMatrix2D ut = (u.viewPart(0, 0, n, 1));
//			System.out.println(ut);
			
//			x2d = factory2d.make(x.toArray(), 1);
//			x2d = algebra.transpose(x2d);
			xx = x.copy();
			xx.assign(x.copy().assign(u.viewPart(0, 0, n, 1), Functions.mult), Functions.plus);
//			System.out.println(xx);
			
			go = xx.aggregate(Functions.min, Functions.identity);
//			System.out.println(go);
			
			if (go > 0) {
				ans1 = algebra.mult(algebra.transpose(xx), Q);
				ob = algebra.mult(ans1, xx).get(0, 0);
				ob = ob / 2 + algebra.mult(algebra.transpose(c), xx).get(0, 0);
//				System.out.println(ob);
				go = Math.min(go, obvalue-ob+EPS);
//				System.out.println(go);
			}
			lamda = 2 * lamda;
			if (lamda >= (1+Math.abs(obvalue))/tolerance) {
	//			System.out.println("The problem seems unbound");
				return;
			}
		}
		
		y = u.viewPart(n, 0, m, 1);
		y.assign(Functions.neg);
		u = u.viewPart(0, 0, n, 1);
		nora = u.aggregate(Functions.min, Functions.identity);
		if(nora < 0){
			nora = -alpha / nora;
		} else if (nora == 0) {
			nora = alpha;
		} else{
			nora = Double.POSITIVE_INFINITY;
		}
//		System.out.println(nora);
		
//		x2d = factory2d.make(x.toArray(), 1);
		u.assign(x, Functions.mult);
		DoubleMatrix2D uTrans = algebra.transpose(u.copy());
//		DoubleMatrix2D ww1 = algebra.mult(uTrans, Q);
		double w1 = algebra.mult(algebra.mult(uTrans, Q), u).get(0, 0);
//		DoubleMatrix2D ww2 = algebra.mult(uTrans, algebra.transpose(gg));
		double w2 = -algebra.mult(uTrans, gg).get(0, 0);
//		System.out.println(w2);
		
//		double w1 = ww1.get(0, 0);
//		double w2 = ww2.get(0, 0);
		if (w1 > 0)
			nora = Math.min(w2/w1, nora);
		if (nora == Double.POSITIVE_INFINITY)
			ob = Double.NEGATIVE_INFINITY;
		else{
			x.assign(u, Functions.plusMult(nora));
//			x = x2d.viewRow(0);
			ob = algebra.mult(algebra.mult(algebra.transpose(x), Q), x).get(0,0);
			ob = ob / 2 + algebra.mult(algebra.transpose(c), x).get(0, 0);
//			System.out.println(ob);
		}
		// End Phase 2
	}


	private void callSPPphase1() {

		DoubleMatrix2D dx = new DenseDoubleMatrix2D(n, 1).assign(1);
		dx.assign(x.viewPart(0, 0, n, 1), Functions.div);
//		System.out.println(dx);
		DoubleMatrix2D dxdx = dx.copy().assign(dx, Functions.mult);
//		System.out.println(dxdx);
		factory2d = DoubleFactory2D.sparse;
		DoubleMatrix2D DD = factory2d.diagonal(dxdx.viewColumn(0));
//		System.out.println(DD);
		DoubleMatrix2D[][] parts1 = {
				{DD, algebra.transpose(A)},
				{A, factory2d.make(m, m)}
		};
		DoubleMatrix2D ans1 = factory2d.compose(parts1);
//		System.out.println(ans1);
		DoubleMatrix2D[][] parts2 = {
				{dx, factory2d.make(n, 1)},
				{factory2d.make(m, 1), a}
		};
		DoubleMatrix2D ans2 = factory2d.compose(parts2);
//		System.out.println(ans2);
		DoubleMatrix2D ans = algebra.solve(ans1, ans2);
//		System.out.println(ans);
		
		DoubleMatrix2D y1 = ans.viewPart(n, 0, m, 1);
		DoubleMatrix2D y2 = ans.viewPart(n, 1, m, 1);
//		System.out.println(y2);
		dx = ans = DD = null;
		
		double w1 = (1/ob - algebra.mult(algebra.transpose(a), y1).get(0, 0));
		w1 = w1 / (1/(ob*ob) - algebra.mult(algebra.transpose(a), y2).get(0, 0));
		double w2 = 1/((ob*ob) - algebra.mult(algebra.transpose(a), y2).get(0, 0));
//		System.out.println(w2);
		
		y1 = y1.assign(y2, Functions.minusMult(w1)); // y1=y1-w1*y2
		y2 = y2.assign(Functions.mult(-w2));
//		System.out.println(y2);
		
		w1 = algebra.mult(algebra.transpose(b), y1).get(0, 0);
		w2 = algebra.mult(algebra.transpose(b), y2).get(0, 0);
//		System.out.println(w1);
		
		y1 = y1.assign(Functions.div(1+w1));
		y2 = y2.assign(y1, Functions.minusMult(w2));
//		System.out.println(y2);
		
		factory2d = DoubleFactory2D.dense;
		
		DoubleMatrix2D tmp1 = x.copy().viewPart(0, 0, n, 1);
		DoubleMatrix2D tmp2 = algebra.transpose(y2.copy()).assign(Functions.neg);
		tmp2 = algebra.mult(tmp2, A);
		tmp2 = algebra.transpose(tmp2);
		DoubleMatrix2D part1 = tmp1.copy().assign(tmp2, Functions.mult);
//		DoubleArrayList list = new DoubleArrayList(tmp2.viewColumn(0).toArray());
		
		tmp2 = algebra.transpose(y2);
		tmp2 = algebra.mult(tmp2, a);
		double value = x.get(n, 0) * (1 - tmp2.get(0, 0));
		DoubleMatrix2D part2 = factory2d.make(1, 1, value);
		
		DoubleMatrix2D part3 = factory2d.make(1, 1, w2 / (1+w1));
		
		DoubleMatrix2D u = factory2d.appendRows(part1, part2);
		u = factory2d.appendRows(u, part3);
//		System.out.println(u);
		
//		double part2 = x.get(n, 0) * (1 - tmp2.get(0, 0)); 
//		list.add(x.get(n, 0) * (1 - tmp2.get(0, 0)));
//		double part3 = w2 / (1+w1);
//		list.add(w2 / (1+w1));
		
		
		
		tmp2 = algebra.transpose(y1);
		tmp2 = algebra.mult(tmp2, A);
		tmp2 = algebra.transpose(tmp2);
		part1 = tmp1.copy().assign(tmp2, Functions.mult);
		
		tmp2 = algebra.transpose(y1);
		tmp2 = algebra.mult(tmp2, a);
		value = x.get(n, 0) * tmp2.get(0, 0);
		part2 = factory2d.make(1, 1, value);
		
		part3 = factory2d.make(1, 1, 1/(1+w1));
		
		DoubleMatrix2D v = factory2d.appendRows(part1, part2);
		v = factory2d.appendRows(v, part3);
//		System.out.println(v);
		
//		DoubleMatrix2D tmp = x.copy().viewPart(0, 0, n, 1);
//		DoubleMatrix2D y22 = DoubleFactory2D.dense.make(y2.toArray(), 1);
//		y22.assign(F.neg);
//		tmp.assign(algebra.mult(y22, A).viewRow(0), F.mult);
//		DoubleArrayList list = new DoubleArrayList(tmp.toArray());
//		list.add(x.get(n) * (1 - algebra.mult(y2, a)));
//		list.add(w2/(1+w1));
//		list.trimToSize();
//		DoubleMatrix1D u = factory1d.make(list);
//		System.out.println(u);
		
//		tmp = x.copy().viewPart(0, n);
//		DoubleMatrix2D y11 = DoubleFactory2D.dense.make(y1.toArray(), 1);
//		tmp.assign(algebra.mult(y11, A).viewRow(0), F.mult);
//		list = new DoubleArrayList(tmp.toArray());
//		list.add(x.get(n) * (algebra.mult(y1, a)));
//		list.add(1/(1+w1));
//		list.trimToSize();
//		DoubleMatrix1D v = factory1d.make(list);
//		System.out.println(v);
		
		double minVal = u.copy().assign(v, Functions.minusMult(z)).aggregate(Functions.min, Functions.identity);
//		System.out.println(minVal);
		if ( minVal >= 0 ) {
			y = y2.copy().assign(y1, Functions.plusMult(z));
			z = algebra.mult(algebra.transpose(b), y).get(0, 0);
//			System.out.println(z);
		}
		y1 = y2 = null;
		
		u.assign(v, Functions.minusMult(z));
		DoubleMatrix2D ones = new DenseDoubleMatrix2D(n+2, 1).assign(1);
		u.assign(ones, Functions.minusMult((ob-z)/(n+2)));
//		System.out.println(u);
		
		nora = u.aggregate(Functions.max, Functions.identity);
//		System.out.println(nora);
		
		if(nora == u.get(n, 0)){
			alpha = 1;
		}
		
		v.assign(ones.assign(u, Functions.minusMult(alpha/nora))); // v=ones-(alpha/nora)*u
		x.assign(v.viewPart(0, 0, n+1, 1), Functions.mult);
		x.assign(Functions.div(v.get(n+1, 0)));
//		System.out.println(nora);
		
	}



	@Override
	public DoubleMatrix2D getSolutionMatrix() {
		solutionMatrix = factory2d.make(x.viewPart(0, 0, dimB*dimA, 1).viewColumn(0).toArray(), dimB);
		return algebra.transpose(solutionMatrix);
	}
	
	public HashMap getSolutionMapping(){
		
		HashMap re = new HashMap();
		DoubleMatrix1D row;
		for (int i = 0; i < solutionMatrix.rows(); i++) {
			row = solutionMatrix.viewRow(i);
			
			// ...
		}
		
		return re;
		
	}
	
	

	public static void main(String[] args) {
		
//		double[][] vals1 = {
//				{0,1,0,1},
//				{0,0,1,0},
//				{1,0,0,0},
//				{0,0,0,0}
//		};
//		DoubleMatrix2D NA = new DenseDoubleMatrix2D(vals1);
//		
//		double[][] vals2 = {
//				{0,1,0,1,0},
//				{0,0,1,0,0},
//				{1,0,0,0,1},
//				{0,0,0,0,0},
//				{0,0,0,0,0}
//		};
//		DoubleMatrix2D NB = new DenseDoubleMatrix2D(vals2);
//		
//		double[][] vals3 = {
//				{.2,.1,.1,.1,.1},
//				{.1,.2,.1,.1,.1},
//				{.1,.1,.2,.1,.1},
//				{.1,.1,.1,.2,.1},
//		};
//		DoubleMatrix2D NSS = new DenseDoubleMatrix2D(vals3);
		
		
		
		double[][] vals1 = {
				{0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
		};
		DoubleMatrix2D NA = new DenseDoubleMatrix2D(vals1);
		     
		double[][] vals2 = {
				{0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0},
			    {0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
		};
		DoubleMatrix2D NB = new DenseDoubleMatrix2D(vals2);
		
		
		double[][] vals3 = {
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0} ,
			    {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
		};
		DoubleMatrix2D NSS = new DenseDoubleMatrix2D(vals3);
		
		long time = System.currentTimeMillis();
		
		
		
		
		time = System.currentTimeMillis() - time;
	//	System.out.println("Elapsed time is " + time + " millisecondes");
		
//		MNAligner ga = new MNAligner(NA, NB, NSS, 0.5);
//		System.out.println(ga.getSolutionMatrix());
	}

	@Override
	public AdjacencyMatrix getGraphA() {
		return graphA;
	}

	@Override
	public AdjacencyMatrix getGraphB() {
		return graphB;
	}
	
}
