/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cut.cis352.products;

import cut.cis352.products.Product;

/**
 * @author charis
 */
public class ProductStorage {

    private final int id;
    private Product product;
    private int quantity;
    private final int capacity;

    public ProductStorage(int id, Product product, int quantity, int capacity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.capacity = capacity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void remove() {
        this.quantity -= 1;
    }


    public void refill(int amount) {
        quantity += amount;
    }


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ProductStorage{" +
                "id=" + id +
                ", product=" + product.getName() +
                ", quantity=" + quantity +
                '}';
    }
}
