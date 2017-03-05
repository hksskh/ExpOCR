#CREATE SOME USERS
INSERT INTO USERS VALUES
(0,'Some Guy0', 'billybob@gmail.com', 'potato123'),
(1,'Some Guy1', 'jimmyjohn@gmail.com', 'veggie3'),
(2,'Some Guy2', 'falpjack@gmail.com', 'po3423'),
(3,'Some Guy3', 'lemons@illinois.edu', 'pot24123'),
(4,'Some Guy4', 'billybob@yahoo.com', 'banana123'),
(5,'Some Guy5', 'chris@comcast.net', '123lookatme'),
(6,'Some Guy6', 'billy@gmail.com', 'secretstuff'),
(7,'Some Guy7', 'bob@gmail.com', '1245958'),
(8,'Some Guy8', 'salmon@yahoo.com', 'abc123'),
(9,'Some Guy9', 'chicken@gmail.com', 'bad420karma');

#CREATE SOME GROUPS
INSERT INTO GROUPS VALUES
(0, 'Cool Kids'),
(1, 'Cooler Kids'),
(2, NULL),
(3, NULL);

#CREATE SOME MEMBERS
INSERT INTO MEMBERS VALUES
(0,0),
(0,1),
(0,3),
(2,0),
(2,4),
(2,5),
(2,6),
(2,7);

#CREATE SOME TRANSACTIONS
INSERT INTO TRANSACTIONS VALUES
(0, 0, 0, 'Salary', 'Payday baby!', 1000.00, '1000-01-01 00:00:00'),
(1, 0, 4, 'Entertainment', 'Batman Movie', -10.00, '1000-01-01 00:20:00'),
(2, 0, 5, 'Entertainment', 'Batman Movie', -10.00, '1000-01-01 00:20:00'),
(3, 0, 6, 'Entertainment', 'Batman Movie', -10.00, '1000-01-01 00:20:00'),
(4, 0, 7, 'Entertainment', 'Batman Movie', -10.00, '1000-01-01 00:20:00'),
(5, 1, 2, 'Food', 'Chipotle steak burrito', 20.00, '2000-01-01 00:00:00'),
(6, 1, 2, 'Food', 'Micky Deez', -1.23, '2000-03-01 00:00:00'),
(7, 0, 0, 'Salary', 'Payday baby!', 1000.00, '2000-03-01 04:00:00'),
(8, 7, 7, 'Rent', 'This is one expensive apartment', -1000.00, '2000-11-01 00:00:00'),
(9, 3, 4, 'Misc', 'Nothin here but us chickens', 0.01, '2000-11-11 00:00:00');

#CREATE SOME GROUP TRANSACTIONS
INSERT INTO GROUP_TRANSACTIONS VALUES
(0,2,1),
(1,2,2),
(2,2,3),
(3,2,4);


