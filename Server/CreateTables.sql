#CREATE ALL TABLES
CREATE TABLE USERS (U_Id int NOT NULL AUTO_INCREMENT PRIMARY KEY, U_Name varchar(255) NOT NULL, Email varchar(255) NOT NULL, Password varchar(255) NOT NULL);
CREATE TABLE GROUPS (G_Id int AUTO_INCREMENT PRIMARY KEY, G_Name varchar(255));
CREATE TABLE MEMBERS (G_Id int NOT NULL, U_Id int NOT NULL, Foreign Key (G_Id) REFERENCES GROUPS(G_Id), Foreign Key (U_Id) REFERENCES USERS(U_Id));
CREATE TABLE TRANSACTIONS (T_Id int NOT NULL AUTO_INCREMENT PRIMARY KEY, Sender_Id int NOT NULL, Receiver_Id int NOT NULL, Category varchar(255), Memo nvarchar(255), Amount numeric(15,2) NOT NULL, Date datetime NOT NULL, Foreign Key (Sender_Id) REFERENCES USERS(U_Id), Foreign Key (Receiver_Id) REFERENCES USERS(U_Id));
CREATE TABLE GROUP_TRANSACTIONS (GT_Id int NOT NULL AUTO_INCREMENT PRIMARY KEY, G_Id int NOT NULL, T_Id int NOT NULL,  FOREIGN KEY (G_Id) REFERENCES GROUPS(G_Id), FOREIGN KEY (T_Id) REFERENCES TRANSACTIONS(T_Id));
