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
import cut.cis352.product.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;

/**
 * @author charis
 */
public class Controller {

    private final ProductDispenser dispenser;
    private final HashMap<String, ProductStorage> storage;
    private CoinManager coinManager;
    private final HashMap<Integer, Product> products;
    private final MySQLDriver driver;
    private Properties vmProperties;
    private String vm_id;
    private final String storageFilePath;
    private final String productsFilePath;
    private final String vmPropertiesFilePath;


    public Controller(HashMap<String, ProductStorage> storage, HashMap<Integer, Product> products, CoinManager coinManager, Properties dbProperties, Properties vmProperties, String storageFilePath, String productsFilePath, String vmPropertiesFilePath) throws SQLException, ClassNotFoundException {
        this.driver = new MySQLDriver(dbProperties);
        this.coinManager = coinManager;
        coinManager.setDriverInstance(driver);
        this.storageFilePath = storageFilePath;
        this.productsFilePath = productsFilePath;
        this.vmPropertiesFilePath = vmPropertiesFilePath;
        this.vmProperties = vmProperties;
        this.vm_id = this.vmProperties.getProperty("vm.id");
        if (vm_id == null || vm_id.equalsIgnoreCase("")) {
            vm_id = UUID.randomUUID().toString();
            driver.insertVendingMachine(vm_id, this.vmProperties.getProperty("vm.location"), Boolean.valueOf(this.vmProperties.getProperty("vm.operating")), this.vmProperties.getProperty("vm.password"));
            coinManager.setVm_id(vm_id);
            this.storage = generateProductStoragesIDs(storage);
            if (driver.isConnected()) {
                this.storage.values().forEach(productStorage -> {
                    try {
                        driver.insertProductStorage(productStorage.getId(), vm_id, productStorage.getProduct(), productStorage.getQuantity(), productStorage.getCapacity());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                this.getCoinManager().getCoinsStorage().forEach((id, coin) -> {
                    try {
                        driver.insertCoinBalance(vm_id, id, coin.getQuantity());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            if (!saveStorageLocal()) {
                System.exit(1);
            }

            vmProperties.setProperty("vm.id", vm_id);

            if (!saveProperties()) {
                System.exit(1);
            }
        } else {
            if (!driver.checkVendingMachineExistence(vm_id)) {
                vm_id = UUID.randomUUID().toString();
                driver.insertVendingMachine(vm_id, this.vmProperties.getProperty("vm.location"), Boolean.valueOf(this.vmProperties.getProperty("vm.operating")), this.vmProperties.getProperty("vm.password"));
                coinManager.setVm_id(vm_id);
                this.storage = generateProductStoragesIDs(storage);
                if (driver.isConnected()) {
                    this.storage.values().forEach(productStorage -> {
                        try {
                            driver.insertProductStorage(productStorage.getId(), vm_id, productStorage.getProduct(), productStorage.getQuantity(), productStorage.getCapacity());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });

                    this.getCoinManager().getCoinsStorage().forEach((id, coin) -> {
                        try {
                            driver.insertCoinBalance(vm_id, id, coin.getQuantity());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
                if (!saveStorageLocal()) {
                    System.exit(1);
                }
                vmProperties.setProperty("vm.id", vm_id);
                if (!saveProperties()) {
                    System.exit(1);
                }
            } else {
                this.vmProperties = driver.getVendingMachine(vm_id);
                this.storage = storage;
            }

        }

        this.dispenser = new ProductDispenser();

        this.products = products;


    }


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

    private HashMap<String, ProductStorage> generateProductStoragesIDs(HashMap<String, ProductStorage> storage) {
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
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    @SuppressWarnings("Duplicates")
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
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("Duplicates")
    public boolean saveStorageLocal() {
        final StringBuilder builder = new StringBuilder();
        storage.forEach((id, productStorage) -> {
            builder.append(id).append(", ")
                    .append(productStorage.getProduct()).append(", ")
                    .append(productStorage.getQuantity()).append(", ")
                    .append(productStorage.getCapacity()).append("\n");
        });
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(storageFilePath));
            writer.write(builder.toString());
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
