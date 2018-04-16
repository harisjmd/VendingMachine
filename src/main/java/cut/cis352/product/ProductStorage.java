/*
 * Copyright 2018 Charalampos Kozis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cut.cis352.product;

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
