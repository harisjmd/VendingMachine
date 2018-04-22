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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class Transaction {

    private static final Logger LOG = LogManager.getLogger();
    private int id;
    private final int product_id;
    private String storage_id;
    private final Date created;
    private Date completed;
    private double moneyInserted;
    private final double price;
    private double change;
    private boolean canceled;

    public Transaction(int id, int product_id, String storage_id, Date created, Date completed, double moneyInserted, double price, double change, boolean canceled) {
        this.id = id;
        this.product_id = product_id;
        this.storage_id = storage_id;
        this.created = created;
        this.completed = completed;
        this.moneyInserted = moneyInserted;
        this.price = price;
        this.change = change;
        this.canceled = canceled;
    }

    public Transaction(int product_id, double price) {
        this.product_id = product_id;
        this.price = price;
        this.moneyInserted = 0.0;
        this.change = 0.0;
        this.canceled = false;
        this.created = new Date();
        this.completed = null;
    }

    public boolean onCoinInserted(double coinValue) {
        moneyInserted = moneyInserted + coinValue;
        LOG.info("Money Inserted: " + moneyInserted);
        if (moneyInserted > price) {
            change = moneyInserted - price;
            return true;
        } else return moneyInserted == price;
    }

    public double getMoneyInserted() {
        return moneyInserted;
    }

    public void cancel() {
        change = moneyInserted;
        canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public double getChange() {
        return change;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public String getStorage_id() {
        return storage_id;
    }

    public void setStorage_id(String storage_id) {
        this.storage_id = storage_id;
    }

    public Date getCompleted() {
        return completed;
    }

    public void setCompleted(Date completed) {
        this.completed = completed;
    }

    public Date getCreated() {
        return created;
    }

    public int getProduct_id() {
        return product_id;
    }
}
