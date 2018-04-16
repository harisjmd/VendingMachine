package vendingmachine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class CoinManager {

    private final HashMap<Double, Coin> coins;
    private double totalMoney;
    private Transaction currentTransaction;

    CoinManager(ArrayList<Coin> coins) {
        this.coins = new HashMap<>();
        coins.forEach(coin -> {
            this.coins.put(coin.getValue(), coin);
        });
        AtomicReference<Double> totalM = new AtomicReference<>();
        totalM.set(0.0);
        coins.forEach(coin -> {
            totalM.set(totalM.get() + coin.getQuantity() * coin.getValue());
        });
    }

    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    public void setCurrentTransaction(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    public double giveChange() {
        return 0.0;
    }


    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public boolean checkCoin(double coinReceived) {
        return coins.containsKey(coinReceived);
    }

    public void increaseCoinQuantity(double coinValue) {
        coins.get(coinValue).increaseQuantity();
    }


    public String getCalculatedChangeCoins() {
        double change = getCurrentTransaction().getChange();
        if (change != 0.0) {
            double copyChange = change * 100;
            BigDecimal bd = new BigDecimal(change);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            String calChange = "Change: " + bd.doubleValue() + "\n";
            int s, i = 0;
            s = (int) copyChange / 200;
            if (s != 0 && coins.get(2.0).getQuantity() > 0) {
                calChange += "2€ x " + String.valueOf(s) + "\n";
                copyChange -= s * 200;
                for (i = 0; i < s; i++) {
                    coins.get(2.0).decreaseQuantity();
                }
            }

            s = (int) copyChange / 100;
            if (s != 0 && coins.get(1.0).getQuantity() > 0) {
                calChange += "1€ x " + String.valueOf(s) + "\n";
                copyChange -= s * 100;
                for (i = 0; i < s; i++) {
                    coins.get(1.0).decreaseQuantity();
                }
            }

            s = (int) copyChange / 50;
            if (s != 0 && coins.get(0.5).getQuantity() > 0) {
                calChange += "0.5€ x " + String.valueOf(s) + "\n";
                copyChange -= s * 50;
                for (i = 0; i < s; i++) {
                    coins.get(0.5).decreaseQuantity();
                }
            }

            s = (int) copyChange / 20;
            if (s != 0 && coins.get(0.2).getQuantity() > 0) {
                calChange += "0.2€ x " + String.valueOf(s) + "\n";
                copyChange -= s * 20;
                for (i = 0; i < s; i++) {
                    coins.get(0.2).decreaseQuantity();
                }
            }

            s = (int) copyChange / 10;
            if (s != 0 && coins.get(0.1).getQuantity() > 0) {
                calChange += "0.1€ x " + String.valueOf(s) + "\n";
                copyChange -= s * 10;
                for (i = 0; i < s; i++) {
                    coins.get(0.1).decreaseQuantity();
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

            return calChange;
        } else {
            return "0";
        }
    }

    public HashMap<Double, Coin> getCoins() {
        return coins;
    }

    //    public void setChange(double change) {
//        this.change = change;
//    }

//    public double getRemaining() {
//        return remaining;
//    }

}
