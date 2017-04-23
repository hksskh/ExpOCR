#CREATE SOME USERS
INSERT INTO `ExpOCR`.`USERS` (`U_Id`, `U_Name`, `Email`, `Password`, `Vericode`) VALUES ('1', 'Mihika', 'mihikadave@gmail.com', 'Potato123@', '');
INSERT INTO `ExpOCR`.`USERS` (`U_Id`, `U_Name`, `Email`, `Password`, `Vericode`) VALUES ('2', 'Anthony', 'kesongh2@illinois.edu', 'Tomato123@', '');
INSERT INTO `ExpOCR`.`USERS` (`U_Id`, `U_Name`, `Email`, `Password`) VALUES ('3', 'Brianna', 'abc@gmail.com', 'Brianna123@');
INSERT INTO `ExpOCR`.`USERS` (`U_Id`, `U_Name`, `Email`, `Password`) VALUES ('4', 'Dan', 'chicken@gmail.com', 'chicken123@');
INSERT INTO `ExpOCR`.`USERS` (`U_Id`, `U_Name`, `Email`, `Password`) VALUES ('5', 'Conner', 'potato@gmail.com', 'potato123@');

#CREATE SOME TRANSACTIONS
INSERT INTO `ExpOCR`.`TRANSACTIONS` (`T_Id`, `Sender_Id`, `Receiver_Id`, `Category`, `Memo`, `Amount`, `Date`) VALUES ('1', '1', '2', 'Food', 'Subway', '20', '2017-04-01 06:02:10');
INSERT INTO `ExpOCR`.`TRANSACTIONS` (`T_Id`, `Sender_Id`, `Receiver_Id`, `Category`, `Memo`, `Amount`, `Date`) VALUES ('2', '2', '1', 'Clothing', 'ApricotLane', '50', '2017-04-11 06:02:10');
INSERT INTO `ExpOCR`.`TRANSACTIONS` (`T_Id`, `Sender_Id`, `Receiver_Id`, `Category`, `Memo`, `Amount`, `Date`) VALUES ('3', '1', '3', 'Food', 'PizzaHut', '25', '2017-03-15 06:02:10');

#CREATE SOME GROUPS
INSERT INTO GROUPS VALUES
(0, 'All The Buddies'),
(1, 'Only Half The Buddies'),
(2, NULL),
(3, NULL);

#CREATE SOME MEMBERS
INSERT INTO MEMBERS VALUES
(0,1),
(0,2),
(0,3),
(0,4),
(0,5),
(1,1),
(1,3),
(1,5),
(2,4);

#CREATE SOME GROUP TRANSACTIONS
INSERT INTO GROUP_TRANSACTIONS VALUES
(0,0,1,'Food','Dinner with the gang', '40', '2017-05-01 11:32:09'),
(1,0,2,'Food','Dinner with the gang', '-10', '2017-05-01 11:32:09'),
(2,0,3,'Food','Dinner with the gang', '-10', '2017-05-01 11:32:09'),
(3,0,4,'Food','Dinner with the gang', '-10', '2017-05-01 11:32:09'),
(4,0,5,'Food','Dinner with the gang', '-10', '2017-05-01 11:32:09'),
(5,0,3,'Food','Dinner with the gang AGAIN', '60', '2017-05-01 12:32:09'),
(6,0,4,'Food','Dinner with the gang AGAIN', '-35', '2017-05-01 12:32:09'),
(7,0,5,'Food','Dinner with the gang AGAIN', '-25', '2017-05-01 12:32:09');
(8,0,4,'Food','Dinner with the gang AGAIN 2.0', '10', '2017-06-01 12:32:09'),
(9,0,1,'Food','Dinner with the gang AGAIN 2.0', '-10', '2017-06-01 12:32:09');

