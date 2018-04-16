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

package cut.cis352;

import cut.cis352.coin.Coin;
import cut.cis352.coin.CoinManager;
import cut.cis352.gui.UserGUI;
import cut.cis352.product.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class VendingMachine {

    private final ArrayList<ProductStorage> storage;
    private final ArrayList<Coin> coins;
    private CoinManager coinManager;
    private HashMap<Integer, Product> products;
    private final ProductDispenser dispenser = new ProductDispenser();
    private UserGUI userGUI;
    private Controller controller;

    public VendingMachine() {
        this.storage = new ArrayList<>();
        this.products = new HashMap<>();
        this.coins = new ArrayList<>();
    }


    private void init() {
        coinManager = new CoinManager(coins);
        controller = new Controller(storage, products, coinManager);
        userGUI = new UserGUI("VendingMachine", controller);
        userGUI.build();
    }


    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Not valid arguments!\nUsage:\n");
            System.err.println("vendingmachine.jar /path/to/productsfile.txt /path/to/storagefile.txt /path/to/coins.txt");
            System.exit(1);
        }


        VendingMachine vm = new VendingMachine();

        try {
            vm.parseProducts(args[0]);
            vm.parseStorage(args[1]);
            vm.parseCoins(args[2]);

        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        vm.init();
        vm.run();

    }


    private void run() {
        userGUI.showGui();
    }

    private boolean isInt(String str) {

        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDouble(String str) {

        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void parseProducts(String pathToFile) throws IOException {
        File f = new File(pathToFile);
        FileReader fr = new FileReader(f);
        BufferedReader bf = new BufferedReader(fr);

        String inline;

        while ((inline = bf.readLine()) != null) {


            String prodProps[] = inline.split(",");
            if (prodProps.length != 5 || inline.startsWith("#")) {

                System.err.println("Product doesn't have all required fields.\n" + inline + "\nSkipping line..");

            } else {

                boolean error = (!isInt(prodProps[0].trim())) || (!isInt(prodProps[3].trim()) || !isDouble(prodProps[prodProps.length - 1].trim()));

                if (error) {
                    System.err.println("Check id or volume/weight or price is wrong formated.\nMust be int, int and double respectively. Skipping..");
                } else {
                    Product product = null;
                    if (prodProps[1].trim().equals("drink")) {
                        product = new Drink(
                                Integer.parseInt(prodProps[0].trim()),
                                prodProps[1].trim(),
                                prodProps[2].trim(),
                                Double.parseDouble(prodProps[4].trim()),
                                Integer.parseInt(prodProps[3].trim()));


                    } else if (prodProps[1].trim().equals("food")) {
                        product = new Food(
                                Integer.parseInt(prodProps[0].trim()),
                                prodProps[1].trim(),
                                prodProps[2].trim(),
                                Integer.parseInt(prodProps[3].trim()),
                                Double.parseDouble(prodProps[4].trim()));
                    }
                    if (product != null) {
                        products.put(product.getId(), product);
                    }
                }
            }
        }

    }

    private void parseStorage(String pathToFile) throws IOException {
        File f = new File(pathToFile);
        FileReader fr = new FileReader(f);
        BufferedReader bf = new BufferedReader(fr);

        String inline;

        while ((inline = bf.readLine()) != null) {


            String splitted[] = inline.split(",");
            if (splitted.length != 3 || inline.startsWith("#")) {

                System.err.println("Skipping line= " + inline);

            } else {

                boolean error = ((!isInt(splitted[1].trim()) || (!isInt(splitted[2].trim()))));

                if (error) {
                    System.err.println("Storage values must be int type, Skipping..");
                } else {

                    ProductStorage productStorage = new ProductStorage(
                            Integer.parseInt(splitted[0].trim()),
                            products.get(Integer.parseInt(splitted[1].trim())),
                            Integer.parseInt(splitted[2].trim()),
                            5);

                    this.storage.add(productStorage);
                }
            }
        }


    }

    private void parseCoins(String pathToFile) throws IOException {
        File f = new File(pathToFile);
        FileReader fr = new FileReader(f);
        BufferedReader bf = new BufferedReader(fr);

        String inline;

        while ((inline = bf.readLine()) != null) {


            String coinValues[] = inline.split(",");
            if (coinValues.length != 2 || inline.startsWith("#")) {

                System.err.println("Coin doesn't have all required fields.\n" + inline + "\nSkipping line..");

            } else {

                boolean error = (!isDouble(coinValues[0].trim())) || (!isInt(coinValues[1].trim()));

                if (error) {
                    System.err.println("Check value or quantity is wrong formatted.\nMust be double, int respectively. Skipping..");
                } else {

                    this.coins.add(new Coin(Double.parseDouble(coinValues[0].trim()), Integer.parseInt(coinValues[1].trim())));

                }
            }
        }

    }

}
