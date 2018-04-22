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
import cut.cis352.coin.Transaction;
import cut.cis352.product.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author charis
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class Controller {

    private static final Logger LOG = LogManager.getLogger();
    private HashMap<Integer, Transaction> transactions;
    private final ProductDispenser dispenser;
    private HashMap<String, ProductStorage> storage;
    private final CoinManager coinManager;
    private HashMap<Integer, Product> products;
    private final MySQLDriver driver;
    private final Properties vmProperties;
    private String vm_id;
    private final boolean wasConnected;
    private final String storageFilePath;
    private final String productsFilePath;
    private final String vmPropertiesFilePath;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


    public Controller(HashMap<String, ProductStorage> storage, HashMap<Integer, Product> products, CoinManager coinManager, Properties dbProperties, Properties vmProperties, String storageFilePath, String productsFilePath, String vmPropertiesFilePath) throws SQLException {

        this.driver = new MySQLDriver(dbProperties);
        this.dispenser = new ProductDispenser();
        this.coinManager = coinManager;
        this.coinManager.setDriverInstance(driver);
        this.storageFilePath = storageFilePath;
        this.productsFilePath = productsFilePath;
        this.vmPropertiesFilePath = vmPropertiesFilePath;
        this.vmProperties = vmProperties;
        this.transactions = retrieveTransactions();
        this.wasConnected = Boolean.valueOf(vmProperties.getProperty("vm.db.wasConnected"));
        this.vm_id = this.vmProperties.getProperty("vm.id");

        // for first startup or vm not in db yet
        if (vm_id == null || vm_id.equalsIgnoreCase("") || (driver.isConnected() && !driver.checkVendingMachineExistence(vm_id))) {
            initializeForFirstStartup(storage);
        } else {

            this.storage = storage;

            if (driver.isConnected()) {
                products = driver.getAvailableProducts();

                /*
                 * if was connected to db, last time powered on, then db should have last attributes states.
                 * Getting last states from db, products storage + coins storage + vm properties
                 *
                 * */
                if (wasConnected) {
                    this.coinManager.setCoinsStorage(driver.getCoinsAndCoinsStorage(vm_id));
                    this.storage = driver.getProductStorage(vm_id);
                    updateProperties();
                }
                /*
                 * if wasn't connected to db, last time powered on, then we have to update db first with
                 * products storage + coins storage + vm properties
                 *
                 * */
                else {

                    this.storage.values().forEach(productStorage -> {
                        try {
                            driver.updateStorage(productStorage.getId(), productStorage.getProduct(), productStorage.getQuantity());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });

                    this.coinManager.getCoinsStorage().forEach((id, coin) -> {
                        try {
                            driver.updateCoinQuantity(vm_id, id, coin.getQuantity());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });

                    driver.updateVendingMachine(
                            vm_id,
                            vmProperties.getProperty("vm.location"),
                            Boolean.valueOf(vmProperties.getProperty("vm.operating")),
                            vmProperties.getProperty("vm.password")
                    );

                    this.transactions.values().forEach(transaction -> {
                        try {
                            driver.insertTransaction(
                                    transaction.getProduct_id(),
                                    transaction.getStorage_id(),
                                    vm_id,
                                    transaction.getMoneyInserted(),
                                    transaction.getChange(),
                                    dateFormat.format(transaction.getCreated()),
                                    transaction.getCompleted() == null ? null : dateFormat.format(transaction.getCompleted()),
                                    transaction.isCanceled()

                            );
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                    });

                    this.transactions = new HashMap<>();
                    if (!saveTransactions()) {
                        System.exit(1);
                    }
                }

                this.vmProperties.setProperty("vm.db.wasConnected", String.valueOf(true));
            } else {
                this.vmProperties.setProperty("vm.db.wasConnected", String.valueOf(false));
            }

            this.products = products;

            // save properties locally
            if (!saveProperties()) {
                System.exit(1);
            }

            // save product storage locally
            if (!saveStorageLocal()) {
                System.exit(1);
            }

            // save products locally
            if (!saveProductsLocal()) {
                System.exit(1);
            }

            // save coins storage locally
            if (!this.coinManager.saveCoinsStorageLocal()) {
                System.exit(1);
            }

        }
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    @SuppressWarnings("WhileLoopReplaceableByForEach")
    public String decreaseStorage(int product_id) {
        Iterator<ProductStorage> it = storage.values().iterator();

        while (it.hasNext()) {
            ProductStorage s = it.next();

            if (s.getProduct() == product_id && s.getQuantity() > 0) {
                s.remove();
                if (driver.isConnected()) {
                    try {
                        driver.updateStorage(s.getId(), product_id, s.getQuantity());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (!saveStorageLocal()) {
                    System.exit(1);
                }
                return s.getId();
            }
        }

        return null;
    }

    private HashMap<String, ProductStorage> generateProductStorageIDs(HashMap<String, ProductStorage> storage) {
        HashMap<String, ProductStorage> toReturn = new HashMap<>();
        storage.values().forEach(productStorage -> {
            String id = UUID.nameUUIDFromBytes((productStorage.getId() + UUID.randomUUID().toString()).getBytes()).toString();
            toReturn.put(id, new ProductStorage(id, productStorage.getProduct(), productStorage.getQuantity(), productStorage.getCapacity()));
        });
        return toReturn;
    }

    public HashMap<String, ProductStorage> getStorage() {
        return storage;
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

    public MySQLDriver getDriver() {
        return driver;
    }

    public Properties getVmProperties() {
        return vmProperties;
    }

    public String getVm_id() {
        return vm_id;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean saveProperties() {
        String licence = "#\n" +
                "# Copyright 2018 Charalampos Kozis\n" +
                "#\n" +
                "# Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "# you may not use this file except in compliance with the License.\n" +
                "# You may obtain a copy of the License at\n" +
                "#\n" +
                "#     http://www.apache.org/licenses/LICENSE-2.0\n" +
                "#\n" +
                "# Unless required by applicable law or agreed to in writing, software\n" +
                "# distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "# See the License for the specific language governing permissions and\n" +
                "# limitations under the License.\n" +
                "#";
        try {
            vmProperties.store(new FileWriter(vmPropertiesFilePath), licence);
            LOG.info("Saved Properties to " + vmPropertiesFilePath);
            return true;
        } catch (IOException e) {
            LOG.fatal("Failed to save Properties to " + vmPropertiesFilePath);
            e.printStackTrace();
            return false;
        }
    }


    @SuppressWarnings({"Duplicates", "WeakerAccess"})
    public boolean saveProductsLocal() {
        final StringBuilder builder = new StringBuilder();
        products.forEach((id, product) -> {
            builder.append(id).append(", ")
                    .append(product.getCategory()).append(", ")
                    .append(product.getName()).append(", ");
            if (product instanceof Drink) {
                builder.append(((Drink) product).getVolume()).append(", ");
            } else if (product instanceof Food) {
                builder.append(((Food) product).getWeight()).append(", ");
            }
            builder.append(product.getPrice()).append("\n");

        });
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(productsFilePath));
            writer.write(builder.toString());
            writer.close();
            LOG.info("Saved Products to " + productsFilePath);
            return true;
        } catch (IOException e) {
            LOG.fatal("Failed to save Products to " + productsFilePath);
            e.printStackTrace();
            return false;
        }
    }

    private void initializeForFirstStartup(HashMap<String, ProductStorage> storage) throws SQLException {
        if (vm_id == null || vm_id.equalsIgnoreCase("")) {
            this.vm_id = UUID.randomUUID().toString();
            this.storage = generateProductStorageIDs(storage);
        } else {
            this.storage = storage;
        }

        this.coinManager.setVm_id(vm_id);


        this.vmProperties.setProperty("vm.id", vm_id);

        // if connected to db register and save vm storage,coinsStorage
        if (driver.isConnected()) {

            driver.insertVendingMachine(vm_id, this.vmProperties.getProperty("vm.location"), Boolean.valueOf(this.vmProperties.getProperty("vm.operating")), this.vmProperties.getProperty("vm.password"));


            this.storage.values().forEach(productStorage -> {
                try {
                    driver.insertProductStorage(productStorage.getId(), vm_id, productStorage.getProduct(), productStorage.getQuantity(), productStorage.getCapacity());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });


            this.coinManager.getCoinsStorage().forEach((id, coin) -> {
                try {
                    driver.insertCoinBalance(vm_id, id, coin.getQuantity());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });


            this.transactions.values().forEach(transaction -> {
                try {
                    driver.insertTransaction(
                            transaction.getProduct_id(),
                            transaction.getStorage_id(),
                            vm_id,
                            transaction.getMoneyInserted(),
                            transaction.getChange(),
                            dateFormat.format(transaction.getCreated()),
                            transaction.getCompleted() == null ? null : dateFormat.format(transaction.getCompleted()),
                            transaction.isCanceled()

                    );
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            });

            this.transactions = new HashMap<>();
            if (!saveTransactions()) {
                System.exit(1);
            }

            this.products = driver.getAvailableProducts();
            this.vmProperties.setProperty("vm.db.wasConnected", String.valueOf(true));
        } else {
            this.vmProperties.setProperty("vm.db.wasConnected", String.valueOf(false));
        }

        // save new properties locally
        if (!saveProperties()) {
            System.exit(1);
        }

        // save new product storage locally
        if (!saveStorageLocal()) {
            System.exit(1);
        }

        // save new products locally
        if (!saveProductsLocal()) {
            System.exit(1);
        }

        // save new coins storage locally
        if (!this.coinManager.saveCoinsStorageLocal()) {
            System.exit(1);
        }
    }

    private HashMap<Integer, Transaction> retrieveTransactions() {
        HashMap<Integer, Transaction> transactionsFound = new HashMap<>();
        String filename = vmProperties.getProperty("vm.transactions_file");
        File f = new File(filename == null || filename.equals("") ? "transactions.txt" : filename);
        FileReader fr;
        try {
            fr = new FileReader(f);
        } catch (FileNotFoundException e) {
            return transactionsFound;
        }

        BufferedReader bf = new BufferedReader(fr);
        String inline;
        try {
            while ((inline = bf.readLine()) != null) {


                String transProps[] = inline.split(",");
                if (transProps.length != 9 || inline.startsWith("#")) {

                    LOG.warn("Transaction doesn't have all required fields.\n" + inline + "\nSkipping line..");

                } else {

                    Transaction transaction = null;
                    try {
                        transaction = new Transaction(
                                Integer.parseInt(transProps[0].trim()),
                                Integer.parseInt(transProps[1].trim()),
                                transProps[2].trim(),
                                dateFormat.parse(transProps[3].trim()),
                                dateFormat.parse(transProps[4].trim()),
                                Double.parseDouble(transProps[5].trim()),
                                Double.parseDouble(transProps[6].trim()),
                                Double.parseDouble(transProps[7].trim()),
                                Boolean.parseBoolean(transProps[8].trim())
                        );
                    } catch (NumberFormatException | ParseException ex) {
                        LOG.error(ex.getLocalizedMessage());
                    }

                    if (transaction != null) {
                        transactionsFound.put(transaction.getId(), transaction);
                    }
                }
            }

            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        LOG.info("Retrieved Transactions from " + f.getAbsolutePath());
        return transactionsFound;
    }

    public boolean saveTransactions() {
        String filename = vmProperties.getProperty("vm.transactions_file") == null || vmProperties.getProperty("vm.transactions_file").equals("") ? "transactions.txt" : vmProperties.getProperty("vm.transactions_file");
        final StringBuilder builder = new StringBuilder();
        transactions.forEach((id, transaction) -> builder.append(id).append(", ")
                .append(transaction.getProduct_id()).append(", ")
                .append(transaction.getStorage_id()).append(", ")
                .append(dateFormat.format(transaction.getCreated())).append(", ")
                .append(transaction.getCompleted() == null ? null : dateFormat.format(transaction.getCompleted())).append(", ")
                .append(transaction.getMoneyInserted()).append(", ")
                .append(transaction.getPrice()).append(", ")
                .append(transaction.getChange()).append(", ")
                .append(transaction.isCanceled()).append("\n"));
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(builder.toString());
            writer.close();
            LOG.info("Saved Transactions to " + filename);
            return true;
        } catch (IOException e) {
            LOG.fatal("Failed to save transactions to " + filename);
            e.printStackTrace();
            return false;
        }
    }

    public HashMap<Integer, Transaction> getTransactions() {
        return transactions;
    }

    @SuppressWarnings("Duplicates")
    public boolean saveStorageLocal() {
        final StringBuilder builder = new StringBuilder();
        storage.forEach((id, productStorage) -> builder.append(id).append(", ")
                .append(productStorage.getProduct()).append(", ")
                .append(productStorage.getQuantity()).append(", ")
                .append(productStorage.getCapacity()).append("\n"));
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(storageFilePath));
            writer.write(builder.toString());
            writer.close();
            LOG.info("Saved Product storage to " + storageFilePath);
            return true;
        } catch (IOException e) {
            LOG.fatal("Failed to save Product storage to " + storageFilePath);
            e.printStackTrace();
            return false;
        }
    }

    private void updateProperties() throws SQLException {
        Properties retrieved = driver.getVendingMachine(vm_id);
        vmProperties.setProperty("vm.location", retrieved.getProperty("vm.location"));
        vmProperties.setProperty("vm.operating", retrieved.getProperty("vm.operating"));
        vmProperties.setProperty("vm.password", retrieved.getProperty("vm.password"));
    }

    @SuppressWarnings("unused")
    public boolean wasConnected() {
        return wasConnected;
    }
}
