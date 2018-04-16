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
import cut.cis352.product.Drink;
import cut.cis352.product.Food;
import cut.cis352.product.Product;
import cut.cis352.product.ProductStorage;

import java.sql.*;
import java.util.HashMap;
import java.util.Properties;

public class MySQLDriver {

    private Connection connection;

    public MySQLDriver(Properties dbProperties) throws ClassNotFoundException, SQLException {
        Class.forName(dbProperties.getProperty("driver"));
        this.connection = DriverManager.getConnection(dbProperties.getProperty("url"), dbProperties);
    }

    public boolean checkVendingMachineExistence(String id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT  vm_id FROM VendingMachine WHERE vm_id=?");
        statement.setString(1, id);
        return statement.executeQuery().next();
    }

    public boolean insertVendingMachine(String id, String location, Boolean operating, String password) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO VendingMachine (`vm_id`, `vm_location`, `operating`, `password`) VALUES (?, ?, ?, ?)");
        statement.setString(1, id);
        statement.setString(2, location);
        statement.setBoolean(3, operating);
        statement.setString(4, password);
        return statement.executeUpdate() == 1;
    }

    public boolean insertProductStorage(String id, String vm_id, int product_id, int quantity, int capacity) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO Storage (`storage_id`, `vm_id`, `product_id`,`quantity`,`capacity`) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, id);
        statement.setString(2, vm_id);
        statement.setInt(3, product_id);
        statement.setInt(4, quantity);
        statement.setInt(5, capacity);
        return statement.executeUpdate() == 1;
    }


    public boolean insertCoinBalance(String vm_id, int coin_id, int quantity) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO balance (`vm_id`, `coin_id`, `quantity`) VALUES (?, ?, ?)");
        statement.setString(1, vm_id);
        statement.setInt(2, coin_id);
        statement.setInt(3, quantity);
        return statement.executeUpdate() == 1;
    }

    public boolean insertProduct(String product_name, int category, double product_price, double product_weight_vol) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO Products (`product_name`, `product_category`, `product_price`,`product_weight_vol`) VALUES (?, ?, ?, ?)");
        statement.setString(1, product_name);
        statement.setInt(2, category);
        statement.setDouble(3, product_price);
        statement.setDouble(4, product_weight_vol);
        return statement.executeUpdate() == 1;
    }

    public int insertTransaction(int product_id, String vm_id, double money_received, double change) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO Transaction (`product`, `vm`,`money_received`,`change`,`created_timestamp`,`completed_timestamp`,`canceled`) VALUES (?, ?, ?, ?, NOW(),NOW(),0)", Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, product_id);
        statement.setString(2, vm_id);
        statement.setDouble(3, money_received);
        statement.setDouble(4, change);
        statement.executeUpdate();
        ResultSet set = statement.getGeneratedKeys();
        if (set.next()) {
            return set.getInt(1);
        } else {
            return -1;
        }
    }

    public HashMap<Integer, Coin> getCoinsAndCoinsStorage(String vm_id) throws SQLException {
        HashMap<Integer, Coin> coins = null;
        PreparedStatement statement = connection.prepareStatement("SELECT  coin_id, coin_value, quantity FROM balance NATURAL JOIN Coin WHERE vm_id=?");
        statement.setString(1, vm_id);
        ResultSet set = statement.executeQuery();
        coins = new HashMap<>();
        while (set.next()) {
            coins.put(set.getInt(1), new Coin(set.getDouble(2), set.getInt(3)));
        }

        return coins;
    }

    public HashMap<String, ProductStorage> getProductStorage(String vm_id) throws SQLException {
        HashMap<String, ProductStorage> productStorage = null;
        PreparedStatement statement = connection.prepareStatement("SELECT  storage_id, product_id, quantity, capacity FROM Storage WHERE vm_id=?");
        statement.setString(1, vm_id);
        ResultSet set = statement.executeQuery();
        productStorage = new HashMap<>();
        while (set.next()) {
            productStorage.put(set.getString(1), new ProductStorage(set.getString(1), set.getInt(2), set.getInt(3), set.getInt(4)));
        }

        return productStorage;
    }

    public Properties getVendingMachine(String vm_id) throws SQLException {
        Properties properties;
        PreparedStatement statement = connection.prepareStatement("SELECT  `vm_id`,`vm_location`,`operating`,`password` FROM VendingMachine WHERE vm_id=?");
        statement.setString(1, vm_id);
        ResultSet set = statement.executeQuery();
        properties = new Properties();
        while (set.next()) {
            properties.setProperty("vm.id", set.getString(1));
            properties.setProperty("vm.location", set.getString(2));
            properties.setProperty("vm.operating", String.valueOf(set.getBoolean(3)));
            properties.setProperty("vm.password", set.getString(4));
        }

        return properties;
    }

    public HashMap<Integer, Product> getAvailableProducts() throws SQLException {
        HashMap<Integer, Product> availableProducts = null;
        PreparedStatement statement = connection.prepareStatement("SELECT  product_id, product_name, category_id, product_price, product_weight_vol FROM Products NATURAL JOIN Product_Category");
        ResultSet set = statement.executeQuery();
        availableProducts = new HashMap<>();

        while (set.next()) {
            if (set.getInt(3) == 1) {
                availableProducts.put(set.getInt(1), new Drink(set.getInt(1), set.getInt(3), set.getString(2), set.getDouble(4), (int) set.getDouble(5)));
            } else if (set.getInt(3) == 2) {
                availableProducts.put(set.getInt(1), new Food(set.getInt(1), set.getInt(3), set.getString(2), (int) set.getDouble(5), set.getDouble(4)));
            }
        }

        return availableProducts;
    }


    public boolean updateTransaction(int transaction_id, String storage_id, double money_received, double change, boolean canceled) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE Transaction SET `storage`=?, `money_received`=?, `change`=?, `completed_timestamp`=NOW(), `canceled`=? WHERE  `transaction_id`=? ");
        statement.setString(1, storage_id);
        statement.setDouble(2, money_received);
        statement.setDouble(3, change);
        statement.setBoolean(4, canceled);
        statement.setInt(5, transaction_id);
        return statement.executeUpdate() == 1;
    }

    public boolean updateStorage(String storage_id, int product_id, int quantity) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE Storage SET `product_id`=?, `quantity`=? WHERE  `storage_id`=? ");
        statement.setInt(1, product_id);
        statement.setInt(2, quantity);
        statement.setString(3, storage_id);
        return statement.executeUpdate() == 1;
    }

    public boolean updateCoinQuantity(String vm_id, int coin_id, int quantity) throws SQLException {

        PreparedStatement statement = connection.prepareStatement("UPDATE balance SET `quantity`=? WHERE  `vm_id`=? AND  `coin_id`=?");
        statement.setInt(1, quantity);
        statement.setString(2, vm_id);
        statement.setInt(3, coin_id);
        return statement.executeUpdate() == 1;
    }

    public boolean updateVendingMachine(String vm_id, String location, boolean operating, String password) throws SQLException {

        PreparedStatement statement = connection.prepareStatement("UPDATE VendingMachine SET `vm_location`=?, `operating`=?, `password`=? WHERE  `vm_id`=?");
        statement.setString(1, location);
        statement.setBoolean(2, operating);
        statement.setString(3, password);
        statement.setString(4, vm_id);
        return statement.executeUpdate() == 1;
    }


    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
