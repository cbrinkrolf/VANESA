package petriNet;

public class SimpleMatrixDouble {

	private double[][] data;
	private int rows;
	private int columns;
	
	public SimpleMatrixDouble(double[][] data){
		this.data = data;
		this.rows = this.data.length;
		this.columns = this.data[0].length;
	}
	
	public void setData(double[][] newData){
		this.data = newData;
	}
	
	public double[][] getData(){
		return this.data;
	}
	
	public void add(SimpleMatrixDouble m){
		double[][] array = m.getData();
		if(this.rows == array.length && this.columns == array[0].length){
			for(int i = 0; i<rows; i++){
				for(int j = 0; j<columns; j++){
					this.data[i][j]+=array[i][j];
				}
			}
		}
	}
	
	public void add(int factor, SimpleMatrixDouble m){
		double[][] array = m.getData();
		if(this.rows == array.length && this.columns == array[0].length){
			for(int i = 0; i<rows; i++){
				for(int j = 0; j<columns; j++){
					this.data[i][j]+= factor*array[i][j];
				}
			}
		}
	}
	
	public double[] getColumn(int index){
		double[] values = new double[this.rows];
		for(int i = 0; i<this.rows; i++){
			values[i] = this.data[i][index];
		}
		return values;
	}
	
	private double[] getRow(int index){
		double[] values = new double[this.columns];
		for(int i = 0; i<this.columns; i++){
			values[i] = this.data[index][i];
		}
		return values;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i<rows; i++){
			for(int j = 0; j<columns; j++){
				sb.append(this.data[i][j]+" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
