#CREATE SOME USERS
INSERT INTO USERS (U_Name, Email, Password) VALUES
('Some Guy0', 'billybob@gmail.com', 'potato123'),
('Some Guy1', 'jimmyjohn@gmail.com', 'veggie3'),
('Some Guy2', 'falpjack@gmail.com', 'po3423'),
('Some Guy3', 'lemons@illinois.edu', 'pot24123'),
('Some Guy4', 'billybob@yahoo.com', 'banana123'),
('Some Guy5', 'chris@comcast.net', '123lookatme'),
('Some Guy6', 'billy@gmail.com', 'secretstuff'),
('Some Guy7', 'bob@gmail.com', '1245958'),
('Some Guy8', 'salmon@yahoo.com', 'abc123'),
('Some Guy9', 'chicken@gmail.com', 'bad420karma');

#CREATE SOME GROUPS
INSERT INTO GROUPS (G_Name) VALUES
('Cool Kids'),
('Cooler Kids');

INSERT INTO GROUPS VALUES
(1, "TestGroup1"),
(3, "TestGroup2");

#CREATE SOME MEMBERS
INSERT INTO MEMBERS (G_Id, U_Id) VALUES
(1,1),
(1,2),
(1,4),
(3,1),
(3,5),
(3,6),
(3,7),
(3,8);

#CREATE SOME TRANSACTIONS
INSERT INTO TRANSACTIONS(Sender_Id, Receiver_Id, Category, Memo, Amount, Date) VALUES
(1, 1, 'Salary', 'Payday baby!', 1000.00, '1000-01-01 00:00:00'),
(1, 5, 'Entertainment', 'Batman Movie', -10.00, '1000-01-01 00:20:00'),
(1, 6, 'Entertainment', 'Batman Movie', -10.00, '1000-01-01 00:20:00'),
(1, 7, 'Entertainment', 'Batman Movie', -10.00, '1000-01-01 00:20:00'),
(1, 8, 'Entertainment', 'Batman Movie', -10.00, '1000-01-01 00:20:00'),
(2, 3, 'Food', 'Chipotle steak burrito', 20.00, '2000-01-01 00:00:00'),
(2, 3, 'Food', 'Micky Deez', -1.23, '2000-03-01 00:00:00'),
(1, 1, 'Salary', 'Payday baby!', 1000.00, '2000-03-01 04:00:00'),
(8, 8, 'Rent', 'This is one expensive apartment', -1000.00, '2000-11-01 00:00:00'),
(4, 5, 'Misc', 'Nothin here but us chickens', 0.01, '2000-11-11 00:00:00');

#CREATE SOME GROUP TRANSACTIONS
INSERT INTO GROUP_TRANSACTIONS (T_Id, G_Id, U_Id, Category, Memo, Amount, Date) VALUES
(1, 1, 1, 'Salary', 'Payday', 500.00, '2017-04-01 06:02:10'),
(2, 1, 5, 'Salary', 'Payday', 465.00, '2017-04-02 12:20:00'),
(3, 1, 6, 'Food', 'Taco Bell', -10.00, '2017-04-03 05:30:00'),
(4, 1, 7, 'Food', 'Dos Reales', -30.00, '2017-04-03 07:20:00'),
(5, 1, 8, 'Entertainment', 'Get Out', -12.00, '2017-04-04 01:20:00'),
(6, 2, 3, 'Food', 'Subway', -5.00, '2017-04-04 08:00:01'),
(7, 2, 3, 'Rent', 'August Rent', -275.00, '2017-04-04 12:06:00'),
(8, 1, 1, 'Entertainment', 'Lil Wayne Concert', -60.00, '2017-04-05 04:00:00'),
(9, 4, 5, 'Misc', 'Not really sure', 0.01, '2017-04-11 01:05:40');