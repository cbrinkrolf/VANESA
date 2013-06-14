package database.brenda;

public class MoleculesPair implements Comparable{

	private String name;
	private int amount;
	private boolean disregard = false;

	public MoleculesPair(String name, int amount, boolean disregarded) {
		this.name = name;
		this.amount = amount;
		this.disregard=disregarded;
	}

	public int compareTo(Object anotherPair) {
		if (!(anotherPair instanceof MoleculesPair))
			throw new ClassCastException("A Pair object expected.");
		int anotherPairAmount = ((MoleculesPair) anotherPair).getAmount();
		return anotherPairAmount - this.amount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public boolean isDisregard() {
		return disregard;
	}

	public void setDisregard(boolean disregard) {
		this.disregard = disregard;
	}


}
