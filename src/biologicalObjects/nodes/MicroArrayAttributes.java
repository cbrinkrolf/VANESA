package biologicalObjects.nodes;

public class MicroArrayAttributes {

	public double getFoldschange() {
		return foldschange;
	}

	public void setFoldschange(double foldschange) {
		this.foldschange = foldschange;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	private double foldschange = 1;
	private int ranking = 1;

	public MicroArrayAttributes() {

	}

}
