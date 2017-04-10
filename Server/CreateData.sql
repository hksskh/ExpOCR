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
INSERT INTO GROUPS ( G_Name) VALUES
( 'Cool Kids'),
( 'Cooler Kids');

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
( 1, 1, 'Salary', 'Payday baby!', 1000.00, '1000-01-01 00:00:00'),
( 1, 5, 'Entertainment', 'Batman Movie', -10.00, '1000-01-01 00:20:00'),
( 1, 6, 'Entertainment', 'Batman Movie', -10.00, '1000-01-01 00:20:00'),
( 1, 7, 'Entertainment', 'Batman Movie', -10.00, '1000-01-01 00:20:00'),
( 1, 8, 'Entertainment', 'Batman Movie', -10.00, '1000-01-01 00:20:00'),
( 2, 3, 'Food', 'Chipotle steak burrito', 20.00, '2000-01-01 00:00:00'),
( 2, 3, 'Food', 'Micky Deez', -1.23, '2000-03-01 00:00:00'),
( 1, 1, 'Salary', 'Payday baby!', 1000.00, '2000-03-01 04:00:00'),
( 8, 8, 'Rent', 'This is one expensive apartment', -1000.00, '2000-11-01 00:00:00'),
( 4, 5, 'Misc', 'Nothin here but us chickens', 0.01, '2000-11-11 00:00:00');

#CREATE SOME GROUP TRANSACTIONS
INSERT INTO GROUP_TRANSACTIONS ( G_Id, T_Id) VALUES
(3,2),
(3,3),
(3,4),
(3,5);

