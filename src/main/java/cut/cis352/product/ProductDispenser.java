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
package cut.cis352.product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author charis
 */
public class ProductDispenser {
    private static final Logger LOG = LogManager.getLogger();

    public void open(){
        LOG.info("Dispense OPEN");
    }

    public void dispense(String productName){
        LOG.info("Dispense " +productName);
    }
    
    public void close(){
        LOG.info("Dispense CLOSE");
    }
    
    
}
