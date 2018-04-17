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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

public class VendingMachine {

    private static final Logger LOG = LogManager.getLogger();
    private final HashMap<String, ProductStorage> storage;
    private final HashMap<Integer, Coin> coinsStorage;
    private CoinManager coinManager;
    private HashMap<Integer, Product> products;
    private final ProductDispenser dispenser = new ProductDispenser();
    private UserGUI userGUI;
    private Controller controller;
    private Properties vmProperties;
    private Properties dbProperties;
    private String storageFilePath;
    private String productsFilePath;
    private String coinsFilePath;
    private String vmPropertiesFilePath;


    public VendingMachine() {
        this.storage = new HashMap<>();
        this.products = new HashMap<>();
        this.coinsStorage = new HashMap<>();
        this.vmProperties = new Properties();
        this.dbProperties = new Properties();

    }


    private void init() throws SQLException {
        coinManager = new CoinManager(coinsStorage, coinsFilePath, vmProperties.getProperty("vm.id"));
        controller = new Controller(
                storage,
                products,
                coinManager,
                dbProperties,
                vmProperties,
                storageFilePath,
                productsFilePath,
                vmPropertiesFilePath);

        userGUI = new UserGUI("VendingMachine", controller);
        userGUI.build();
    }


    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Not valid arguments!\nUsage:\n");
            System.err.println("vendingmachine.jar /path/to/productsfile.txt /path/to/storagefile.txt /path/to/coins.txt vm.properties");
            System.exit(1);
        }


        VendingMachine vm = new VendingMachine();

        try {

            vm.parseProducts(args[0]);
            vm.parseStorage(args[1]);
            vm.parseCoins(args[2]);
            vm.readProperties(args[3]);
        } catch (IOException e) {
            LOG.fatal(e.getMessage());
            System.exit(1);
        }
        try {
            vm.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        vm.run();

    }


    private void run() {
        userGUI.showGui();
    }

    @SuppressWarnings({"BooleanMethodIsAlwaysInverted", "ResultOfMethodCallIgnored"})
    private boolean isInt(String str) {

        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "BooleanMethodIsAlwaysInverted"})
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
        productsFilePath = pathToFile;
        String inline;

        while ((inline = bf.readLine()) != null) {


            String prodProps[] = inline.split(",");
            if (prodProps.length != 5 || inline.startsWith("#")) {

                LOG.warn("Product doesn't have all required fields.\n" + inline + "\nSkipping line..");

            } else {

                boolean error = (!isInt(prodProps[0].trim())) || (!isInt(prodProps[3].trim()) || !isDouble(prodProps[prodProps.length - 1].trim()));

                if (error) {
                    LOG.warn("Check id or volume/weight or price is wrong formated.\nMust be int, int and double respectively. Skipping..");
                } else {
                    Product product = null;
                    if (prodProps[1].trim().equals("1")) {
                        product = new Drink(
                                Integer.parseInt(prodProps[0].trim()),
                                Integer.parseInt(prodProps[1].trim()),
                                prodProps[2].trim(),
                                Double.parseDouble(prodProps[4].trim()),
                                Integer.parseInt(prodProps[3].trim()));


                    } else if (prodProps[1].trim().equals("2")) {
                        product = new Food(
                                Integer.parseInt(prodProps[0].trim()),
                                Integer.parseInt(prodProps[1].trim()),
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
        storageFilePath = pathToFile;
        String inline;

        while ((inline = bf.readLine()) != null) {


            String splitted[] = inline.split(",");
            if (splitted.length != 4 || inline.startsWith("#")) {

                LOG.warn("Skipping line= " + inline);

            } else {

                boolean error = ((!isInt(splitted[1].trim()) || (!isInt(splitted[2].trim())) || (!isInt(splitted[3].trim()))));

                if (error) {
                    LOG.warn("Storage values must be int type, Skipping..");
                } else {

                    ProductStorage productStorage = new ProductStorage(
                            splitted[0].trim(),
                            Integer.parseInt(splitted[1].trim()),
                            Integer.parseInt(splitted[2].trim()),
                            Integer.parseInt(splitted[3].trim()));

                    this.storage.put(productStorage.getId(), productStorage);
                }
            }
        }


    }

    private void parseCoins(String pathToFile) throws IOException {
        File f = new File(pathToFile);
        FileReader fr = new FileReader(f);
        BufferedReader bf = new BufferedReader(fr);
        coinsFilePath = pathToFile;
        String inline;

        while ((inline = bf.readLine()) != null) {


            String coinValues[] = inline.split(",");
            if (coinValues.length != 3 || inline.startsWith("#")) {

                LOG.warn("Coin doesn't have all required fields.\n" + inline + "\nSkipping line..");

            } else {

                boolean error = (!isInt(coinValues[0].trim())) || (!isDouble(coinValues[1].trim())) || (!isInt(coinValues[2].trim()));

                if (error) {
                    LOG.warn("Check id, value or quantity is wrong formatted.\nMust be int, double, int respectively. Skipping..");
                } else {

                    this.coinsStorage.put(Integer.parseInt(coinValues[0]), new Coin(Double.parseDouble(coinValues[1].trim()), Integer.parseInt(coinValues[2].trim())));

                }
            }
        }

    }


    private void readProperties(String vmPropertiesFile) {
        vmPropertiesFilePath = vmPropertiesFile;
        try {
            dbProperties.load(VendingMachine.class.getResourceAsStream("db.properties"));
            vmProperties.load(new BufferedInputStream(new FileInputStream(vmPropertiesFile)));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
