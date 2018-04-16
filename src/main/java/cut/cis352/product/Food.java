package cut.cis352.products;

public class Food extends Product {

    private int weight;

    public Food(int id, String category, String name, int weight, double price) {
        super(id, category, name, price);
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
