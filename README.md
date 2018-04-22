# VendingMachine
An internet connected Vending Machine developed for a University java course

## Dependencies

* mysql.mysql-connector-java:6.0.6
* org.apache.logging.log4j.log4j-api:2.9.1
* org.apache.logging.log4j.log4j-core:2.9.1

## Tools Required

* Maven 3.0
## Build

1. Specify **user** and **password** in db.properties
2. Specify domain/IP in **url** of db.properties

3. ```mvn clean verify package ```

## First Run Configuration

1. Execute mysql/vm.sql to a desired MySQL server
2. Execute mysql/inserts.sql to a desired MySQL server
3. Specify **vm.password** in vm.properties, representing admin password
4. Specify **vm.currency** in vm.properties, representing the currency that vending machine will use
5. Specify **vm.transactions_file** in vm.properties, as the file name of transactions to be saved
6. Specify **vm.location** in vm.properties, representing the location of vending machine
7. Fill out storage.txt with Product Storage with the order of `id, product_id, quantity, capacity`
8. Fill out products.txt with Products available with the order of `id, category_id, name, volume/weight, price`
9. Fill out coins.txt with Coins available with the order of `id, coin_value, quantity`

For more help see example files in examples directory.

## Run

```
vendingmachine-1.0-jar-with-dependencies.jar config/products.txt config/storage.txt config/coins.txt config/vm.properties

```