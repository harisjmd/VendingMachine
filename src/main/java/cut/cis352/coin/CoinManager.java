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

package cut.cis352.coin;

import cut.cis352.MySQLDriver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CoinManager {

    private final HashMap<Integer, Coin> coinsStorage;
    private Transaction currentTransaction;
    private final double[] acceptedCoins;
    private  MySQLDriver driverInstance;
    private final String coinsFilePath;
    private String vm_id;

    public CoinManager(HashMap<Integer, Coin> coinsStorage, String coinsFilePath, String vm_id) {
        this.coinsStorage = coinsStorage;
        this.coinsFilePath = coinsFilePath;
        this.acceptedCoins = new double[coinsStorage.size()];
        this.vm_id = vm_id;
        getTotalMoney();
    }

    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    public void setCurrentTransaction(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    public void setDriverInstance(MySQLDriver driverInstance) {
        this.driverInstance = driverInstance;
    }

    public void setVm_id(String vm_id) {
        this.vm_id = vm_id;
    }


    public double getTotalMoney() {
        AtomicInteger i = new AtomicInteger(0);
        AtomicReference<Double> totalM = new AtomicReference<>();
        totalM.set(0.0);
        coinsStorage.values().forEach(coin -> {

            totalM.set(totalM.get() + coin.getQuantity() * coin.getValue());
            acceptedCoins[i.getAndAdd(1)] = coin.getValue();
        });
        return totalM.get();
    }


    public boolean checkCoin(double coinReceived) {
        for (int i = 0; i < coinsStorage.size(); i++) {
            if (acceptedCoins[i] == coinReceived) {
                return true;
            }
        }
        return false;
    }

    public int getCoinId(double coinReceived) {
        for (int i = 0; i < coinsStorage.size(); i++) {
            if (acceptedCoins[i] == coinReceived) {
                return i;
            }
        }
        return -1;
    }

    public void increaseCoinQuantity(int coin_id) {
        coinsStorage.get(coin_id).increaseQuantity();
        if (driverInstance.isConnected()) {
            try {
                driverInstance.updateCoinQuantity(vm_id, coin_id, coinsStorage.get(coin_id).getQuantity());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @SuppressWarnings("Duplicates")
    private boolean saveCoinsStorageLocal() {
        final StringBuilder builder = new StringBuilder();
        coinsStorage.forEach((id, coin) -> {
            builder.append(id).append(", ").append(coin.getValue()).append(", ").append(coin.getQuantity()).append("\n");
        });
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(coinsFilePath));
            writer.write(builder.toString());
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }


    public String getCalculatedChangeCoins() {
        double change = getCurrentTransaction().getChange();

        if (change != 0.0) {

            double copyChange = change * 100;
            BigDecimal bd = new BigDecimal(change);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            StringBuilder calChange = new StringBuilder("Change: " + bd.doubleValue() + "\n");
            int s, i = 0;
            Iterator<Map.Entry<Integer, Coin>> it = coinsStorage.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Coin> pair = it.next();

                s = (int) copyChange / (int)(pair.getValue().getValue() * 100);
                if (s != 0 && pair.getValue().getQuantity() > 0) {
                    calChange.append(String.valueOf(pair.getValue().getValue())).append("€ x ").append(String.valueOf(s)).append("\n");
                    copyChange -= s * (int) (pair.getValue().getValue() * 100);
                    for (i = 0; i < s; i++) {
                        coinsStorage.get(pair.getKey()).decreaseQuantity();
                    }

                    if (driverInstance.isConnected()) {
                        try {
                            driverInstance.updateCoinQuantity(vm_id, pair.getKey(), coinsStorage.get(pair.getKey()).getQuantity());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (!saveCoinsStorageLocal()) {
                System.exit(1);
            }
            return calChange.toString();
        } else {
            return "0";
        }
    }

    public HashMap<Integer, Coin> getCoinsStorage() {
        return coinsStorage;
    }


}
