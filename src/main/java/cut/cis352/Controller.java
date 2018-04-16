/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vendingmachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
