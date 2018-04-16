package vendingmachine;

public class Transaction {

    private double moneyInserted;
    private final double price;
    private double change;

    public Transaction(double price) {
        this.price = price;
        System.out.println("Price: " + price);
        this.moneyInserted = 0.0;
        this.change = 0.0;
    }

    public boolean onCoinInserted(double coinValue) {
        moneyInserted = moneyInserted + coinValue;
        System.out.println("Money Inserted: " + moneyInserted);
        if (moneyInserted > price) {
            change = moneyInserted - price;
            return true;
        } else return moneyInserted == price;
    }

    public double getMoneyInserted() {
        return moneyInserted;
    }

    public void cancel() {
        change = moneyInserted;
    }


    public double getChange() {
        return change;
    }
}
