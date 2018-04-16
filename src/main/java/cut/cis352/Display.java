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

import cut.cis352.product.ProductStorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author charis
 */
public class Display {

    private final Scanner scanner;

    public Display() {
        this.scanner = new Scanner(System.in);
    }

    public String printMenu(HashMap<String, ProductStorage> current_storage) {
        HashMap<String, Integer> quantities = new HashMap<>();
        HashMap<String, String> storage_ids = new HashMap<>();
        ArrayList<String> idsWithStock = new ArrayList<>();

        current_storage.forEach((k, v) -> {
            if (quantities.containsKey(v.getProduct().getName().toUpperCase())) {
                int current_quantity = quantities.get(v.getProduct().getName().toUpperCase());
                quantities.replace(v.getProduct().getName().toUpperCase(), current_quantity + v.getQuantity());
            } else {
                quantities.put(v.getProduct().getName().toUpperCase(), v.getQuantity());
            }

            if (storage_ids.containsKey(v.getProduct().getName().toUpperCase())) {
                
                if (v.getQuantity() > 0) {
                    String current_ids = storage_ids.get(v.getProduct().getName().toUpperCase());
                    storage_ids.replace(v.getProduct().getName().toUpperCase(), current_ids + ", " + k);
                      idsWithStock.add(k);
                }

            } else {
                if (v.getQuantity() > 0) {
                    storage_ids.put(v.getProduct().getName().toUpperCase(), k);
                    idsWithStock.add(k);
                }
            }

        });

        final StringBuilder menu = new StringBuilder();

        menu.append(
                "\nWelcome to Vending Machine\n\n\n");
        menu.append(
                "Available Products\n\n");
        quantities.forEach(
                (k, v) -> {
                    if (v > 0) {
                        menu.append(k);
                        menu.append(" (");
                        menu.append(storage_ids.get(k));
                        menu.append(")\n");
                    }
                }
        );
        menu.append(
                "\n\nPlease choose a product id:\t");
        String lineInput;

        do {
            System.out.println(menu.toString());
            lineInput = scanner.nextLine();
            if (current_storage.containsKey(lineInput.toUpperCase())) {
                if (idsWithStock.contains(lineInput.toUpperCase())) {
                    break;
                } else {
                    System.out.println("\nNo stock at " + lineInput.toUpperCase());
                }

            } else {
                System.out.println("\nWrong Id.Please select a valid product id");
            }
        } while (true);
        return lineInput;
    }

    public double requestMoneyMessage(double amount) {
        String amountInput;

        while (true) {
            System.out.println("Accepting 0.05, 0.10, 0.20, 0.50, 1, 2 euro coins");
            System.out.println("Please insert " + String.valueOf(amount) + " or c to cancel");
            amountInput = scanner.nextLine();
            if (amountInput.equalsIgnoreCase("c")) {
                return -1.0;
            }

            try {
                return Double.valueOf(amountInput);
            } catch (NumberFormatException e) {
                System.out.println("Error not suiitable coin: " + amountInput);
            }
        }

    }

    public void printPickupProduct(String productName) {
        System.out.println("Please pickup your " + productName);
//        System.out.println("Press enter to continue");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String s = null;
        try {
            s = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (s.length() == 0) {

        }
    }

    public void printChange(String change) {
        System.out.println(change);
    }

}
