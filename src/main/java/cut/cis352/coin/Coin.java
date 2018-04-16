package vendingmachine;

public class Coin {
    private double value;
    private int quantity;

    public Coin(double value, int quantity) {
        this.value = value;
        this.quantity = quantity;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increaseQuantity() {
        this.quantity++;
    }
    public void decreaseQuantity() {
        this.quantity--;
    }

    @Override
    public String toString() {
        return "Coin{" +
                "value=" + value +
                ", quantity=" + quantity +
                '}';
    }
}
