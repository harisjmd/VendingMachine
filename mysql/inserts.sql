INSERT INTO `vm`.`Product_Category` (`category_name`) VALUES ('DRINK'),('FOOD');
SELECT * FROM Product_Category;
INSERT INTO Products (`product_name`, `product_category`, `product_price`,`product_weight_vol`)
VALUES
  ('Coca Cola', 1, 2.00, 300.0),
  ('7-up', 1, 1.00, 200.0),
  ('Mars', 2, 1.00, 100.0),
  ('Lays Salt', 2, .8, 100.0),
  ('Water Bottle', 1, .6, 400.0),
  ('Kean Orange Juice', 1, .8, 550.0),
  ('Kit-Kat', 2, 1.00, 200.0);
SELECT * FROM Products;
INSERT INTO `vm`.`Coin` (`coin_value`) VALUES (0.10),(0.20),(0.50),(1.0),(2.0);
SELECT * FROM vm.Coin;