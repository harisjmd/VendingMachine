package cut.cis352.products;

public class Drink extends Product {

    private int volume;

    public Drink(int id, String category, String name,  double price, int volume) {
        super(id, category, name, price);
        this.volume = volume;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
