# --- !Ups
INSERT INTO `user` VALUES (1,'kevinvanissa@gmail.com','$2a$10$uEwYbR3gcFZgGcxwgvuoHu582WKbcoleUEg//Db76iDIm6BbPkKUW'),(2,'sassyvanjay@yahoo.com','$2a$10$uEwYbR3gcFZgGcxwgvuoHu582WKbcoleUEg//Db76iDIm6BbPkKUW'),(3,'kevinjmiller@yahoo.com','$2a$10$uEwYbR3gcFZgGcxwgvuoHu582WKbcoleUEg//Db76iDIm6BbPkKUW');
INSERT INTO `kitchen` VALUES (1,'kevin_kitchen'),(2,'vanissa_kitchen');
INSERT INTO `dietarypref` VALUES (1,'Vegan','In addition to the abstentions of a vegetarian diet, vegans do not use any product produced by animals, such as eggs, dairy products, or honey.'),(2,'Vegetarian','A vegetarian diet is one which excludes meat. Vegetarians also avoid food containing by-products of animal slaughter, such as animal-derived rennet and gelatin.'),(3,'Semi-vegetarianism','A predominantly vegetarian diet, in which meat is occasionally consumed'),(4,'Lacto vegetarianism','A vegetarian diet that includes certain types of dairy, but excludes eggs and foods which contain animal rennet');
INSERT INTO `recipe` VALUES (1,'Roasted Potatoes',1,'Sweet Roasted Potatoes','ingredient1\ningredient2\ningredient3','instruction1\ninstruction2\ninstruction3','image1.jpg'),(2,'Roasted Corn',2,'Nice Sweet Dish','ingredient1\ningredient2\ningredient3','instruction1\ninstruction2\ninstruction3','image2.jpg'),(3,'Fried Rice',4,'Spicy Fried Rice','ingredient1\ningredient2\ningredient3','instruction1\ninstruction2\ninstruction3','image3.png');
INSERT INTO `profile` VALUES (1,'Kevin','Miller'),(2,'Vanissa','Miller'),(3,'Kevin','Dundee');
INSERT INTO `stores` VALUES (1,1,'2 LBS Yam'),(2,2,'2 PINCH Salt'),(3,2,'2 LBS Sugar');
INSERT INTO `mealplan` VALUES (1,1,'2018-01-01 04:55:25'),(2,2,'2018-01-01 04:55:25'),(3,3,'2018-01-01 04:55:25');
INSERT INTO `medicalprofile` VALUES (1,'Z91011'),(1,'Z91012'),(2,'Z91013'),(3,'Z91010');
INSERT INTO `dietaryprofile` VALUES (1,1),(1,3),(2,2);

# --- !Downs

DELETE FROM user;

DELETE FROM kitchen;

DELETE FROM dietarypref;

DELETE FROM recipe;

DELETE FROM profile;

DELETE FROM stores;

DELETE FROM mealplan;

DELETE FROM medicalprofile;

DELETE FROM dietaryprofile;
