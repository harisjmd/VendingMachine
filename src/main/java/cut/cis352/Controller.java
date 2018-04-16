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
package cut.cis352;

import cut.cis352.coin.CoinManager;
import cut.cis352.product.Product;
import cut.cis352.product.ProductDispenser;
import cut.cis352.product.ProductStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author charis
 */
public class Controller {

    private final ProductDispenser dispenser;
    private final ArrayList<ProductStorage> storage;
    private CoinManager coinManager;
    private final HashMap<Integer,Product> products;
    private Product currentProduct = null;
    private int change = 0;
    private int remaining = 0;


    public Controller( ArrayList<ProductStorage> storage, HashMap<Integer, Product> products, CoinManager coinManager) {
        this.dispenser = new ProductDispenser();
        this.storage = storage;
        this.products = products;
        this.coinManager = coinManager;
    }

    public ArrayList<ProductStorage> getStorage() {
        return storage;
    }

    public void decreaseStorage(String item_name){
        Iterator<ProductStorage> it = storage.iterator();

        while (it.hasNext()) {
            ProductStorage s = it.next();

            if (s.getProduct().getName().equals(item_name) && s.getQuantity() > 0) {
                s.remove();
                break;
            }
        }

    }

    public void dispenseProduct(Product product){
        dispenser.open();
        dispenser.close();
    }

    public void receiveCoin(String coinValue){
        double coin = Double.parseDouble(coinValue);
    }

    public void onCancel(){
        currentProduct = null;

    }

    public HashMap<Integer, Product> getProducts() {
        return products;
    }


    public ProductDispenser getDispenser() {
        return dispenser;
    }

    public CoinManager getCoinManager() {
        return coinManager;
    }

    public void saveProducts(){

    }

    public void saveStorage(){

    }

    public void saveCoins(){

    }
}
