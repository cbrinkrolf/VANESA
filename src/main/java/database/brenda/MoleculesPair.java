package database.brenda;

public class MoleculesPair implements Comparable<MoleculesPair> {
    private String name;
    private int amount;
    private boolean disregard;

    public MoleculesPair(String name, int amount, boolean disregarded) {
        this.name = name;
        this.amount = amount;
        this.disregard = disregarded;
    }

    public int compareTo(MoleculesPair anotherPair) {
        return anotherPair.getAmount() - this.amount;
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
