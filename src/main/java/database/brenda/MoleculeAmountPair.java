package database.brenda;

public class MoleculeAmountPair implements Comparable<MoleculeAmountPair> {
    private final String name;
    private final int amount;
    private boolean disregard;

    public MoleculeAmountPair(String name, int amount, boolean disregarded) {
        this.name = name;
        this.amount = amount;
        this.disregard = disregarded;
    }

    public int compareTo(MoleculeAmountPair anotherPair) {
        return anotherPair.getAmount() - amount;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isDisregard() {
        return disregard;
    }

    public void setDisregard(boolean disregard) {
        this.disregard = disregard;
    }
}
