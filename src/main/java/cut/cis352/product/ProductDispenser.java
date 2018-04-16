/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cut.cis352.products;

/**
 *
 * @author charis
 */
public class ProductDispenser {
    
    public void open(){
        System.out.println("Dispense OPEN");
    }

    public void dispense(String productName){
        System.out.println("Dispense " +productName);
    }
    
    public void close(){
        System.out.println("Dispense CLOSE");
    }
    
    
}
