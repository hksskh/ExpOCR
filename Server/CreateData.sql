#CREATE SOME USERS
INSERT INTO `ExpOCR`.`USERS` (`U_Id`, `U_Name`, `Email`, `Password`, `Vericode`) VALUES ('1', 'Mihika', 'mihikadave@gmail.com', 'Potato123@', '');
INSERT INTO `ExpOCR`.`USERS` (`U_Id`, `U_Name`, `Email`, `Password`, `Vericode`) VALUES ('2', 'Anthony', 'kesongh2@illinois.edu', 'Tomato123@', '');
INSERT INTO `ExpOCR`.`USERS` (`U_Id`, `U_Name`, `Email`, `Password`) VALUES ('3', 'Brianna', 'abc@gmail.com', 'Brianna123@');


#CREATE SOME TRANSACTIONS
INSERT INTO `ExpOCR`.`TRANSACTIONS` (`T_Id`, `Sender_Id`, `Receiver_Id`, `Category`, `Memo`, `Amount`, `Date`) VALUES ('1', '1', '2', 'Food', 'Subway', '20', '2017-04-01 06:02:10');
INSERT INTO `ExpOCR`.`TRANSACTIONS` (`T_Id`, `Sender_Id`, `Receiver_Id`, `Category`, `Memo`, `Amount`, `Date`) VALUES ('2', '2', '1', 'Clothing', 'ApricotLane', '50', '2017-04-11 06:02:10');
INSERT INTO `ExpOCR`.`TRANSACTIONS` (`T_Id`, `Sender_Id`, `Receiver_Id`, `Category`, `Memo`, `Amount`, `Date`) VALUES ('3', '1', '3', 'Food', 'PizzaHut', '25', '2017-03-15 06:02:10');

