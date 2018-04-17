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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CoinManager {

    private HashMap<Integer, Coin> coinsStorage;
    private Transaction currentTransaction;
    private final HashMap<Integer, Double> acceptedCoins;
    private MySQLDriver driverInstance;
    private final String coinsFilePath;
    private String vm_id;

    public CoinManager(HashMap<Integer, Coin> coinsStorage, String coinsFilePath, String vm_id) {
        this.coinsStorage = coinsStorage;
        this.coinsFilePath = coinsFilePath;
        this.acceptedCoins = new HashMap<>();
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
        AtomicReference<Double> totalM = new AtomicReference<>();
        totalM.set(0.0);
        coinsStorage.forEach((id, coin) -> {

            totalM.set(totalM.get() + coin.getQuantity() * coin.getValue());
            acceptedCoins.put(id, coin.getValue());
        });
        return totalM.get();
    }


    public boolean checkCoin(double coinReceived) {
        return acceptedCoins.containsValue(coinReceived);
    }

    public int getCoinId(double coinReceived) {
      Iterator<Map.Entry<Integer,Double>> it = acceptedCoins.entrySet().iterator();
      while (it.hasNext()){
          Map.Entry<Integer,Double> pair = it.next();

          if(pair.getValue() == coinReceived){
              return pair.getKey();
          }
      }

      return  -1;
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
        if(!saveCoinsStorageLocal()){
            System.exit(1);
        }

    }

    @SuppressWarnings("Duplicates")
    public boolean saveCoinsStorageLocal() {
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
            String calChange = bd.doubleValue() + ". " ;
            int s, i = 0;
            s = (int) copyChange / 200;
            if (s != 0 && coinsStorage.get(getCoinId(2.0)).getQuantity() > 0) {
                calChange += "2€ x " + String.valueOf(s) + ", ";
                copyChange -= s * 200;
                for (i = 0; i < s; i++) {
                    coinsStorage.get(getCoinId(2.0)).decreaseQuantity();
                }
            }

            s = (int) copyChange / 100;
            if (s != 0 && coinsStorage.get(getCoinId(1.0)).getQuantity() > 0) {
                calChange += "1€ x " + String.valueOf(s) + ", ";
                copyChange -= s * 100;
                for (i = 0; i < s; i++) {
                    coinsStorage.get(getCoinId(1.0)).decreaseQuantity();
                }
            }

            s = (int) copyChange / 50;
            if (s != 0 && coinsStorage.get(getCoinId(0.5)).getQuantity() > 0) {
                calChange += "0.5€ x " + String.valueOf(s) + ", ";
                copyChange -= s * 50;
                for (i = 0; i < s; i++) {
                    coinsStorage.get(getCoinId(0.5)).decreaseQuantity();
                }
            }

            s = (int) copyChange / 20;
            if (s != 0 && coinsStorage.get(getCoinId(0.2)).getQuantity() > 0) {
                calChange += "0.2€ x " + String.valueOf(s) + ", ";
                copyChange -= s * 20;
                for (i = 0; i < s; i++) {
                    coinsStorage.get(getCoinId(0.2)).decreaseQuantity();
                }
            }

            s = (int) copyChange / 10;
            if (s != 0 && coinsStorage.get(getCoinId(0.1)).getQuantity() > 0) {
                calChange += "0.1€ x " + String.valueOf(s) + ", ";
                copyChange -= s * 10;
                for (i = 0; i < s; i++) {
                    coinsStorage.get(getCoinId(0.1)).decreaseQuantity();
                }
            }

//            s = (int) copyChange / 5;
//            if (s != 0 && coins.get(0.05).getQuantity() > 0) {
//                calChange += "0.05€ x " + String.valueOf(s) + "\n";
//                copyChange -= s * 5;
//                for (i = 0; i < s; i++) {
//                    coins.get(0.05).decreaseQuantity();
//                }
//            }

            if (driverInstance.isConnected()) {
                coinsStorage.forEach((id, coin) -> {
                    try {
                        driverInstance.updateCoinQuantity(vm_id, id, coin.getQuantity());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            if (!saveCoinsStorageLocal()) {
                System.exit(1);
            }
            return calChange;
        } else {
            return "0";
        }
    }

    public void setCoinsStorage(HashMap<Integer, Coin> coinsStorage) {
        this.coinsStorage = coinsStorage;
        getTotalMoney();
    }

    public HashMap<Integer, Coin> getCoinsStorage() {
        return coinsStorage;
    }


}
