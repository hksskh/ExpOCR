-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: localhost    Database: ExpOCR
-- ------------------------------------------------------
-- Server version	5.7.17

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `GROUPS`
--

DROP TABLE IF EXISTS `GROUPS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GROUPS` (
  `G_Id` int(11) NOT NULL AUTO_INCREMENT,
  `G_Name` varchar(255) NOT NULL,
  PRIMARY KEY (`G_Id`),
  UNIQUE KEY `G_Id_UNIQUE` (`G_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `GROUP_TRANSACTIONS`
--

DROP TABLE IF EXISTS `GROUP_TRANSACTIONS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GROUP_TRANSACTIONS` (
  `GT_Id` int(11) NOT NULL AUTO_INCREMENT,
  `G_Id` int(11) NOT NULL,
  `U_Id` int(11) NOT NULL,
  `Category` varchar(255) NOT NULL,
  `Memo` varchar(255) NOT NULL,
  `Amount` decimal(15,2) NOT NULL,
  `Date` datetime NOT NULL,


  PRIMARY KEY (`GT_Id`),
  UNIQUE KEY `GT_Id_UNIQUE` (`GT_Id`),
  KEY `G_Id` (`G_Id`),
  KEY `U_Id` (`U_Id`),
  CONSTRAINT `group_transactions_ibfk_1` FOREIGN KEY (`G_Id`) REFERENCES `GROUPS` (`G_Id`),
  CONSTRAINT `group_transactions_ibfk_2` FOREIGN KEY (`U_Id`) REFERENCES `USERS` (`U_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MEMBERS`
--

DROP TABLE IF EXISTS `MEMBERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MEMBERS` (
  `GU_Id` int(11) NOT NULL AUTO_INCREMENT,
  `G_Id` int(11) NOT NULL,
  `U_Id` int(11) NOT NULL,
  PRIMARY KEY (`GU_Id`),
  UNIQUE KEY `M_Id_UNIQUE` (`GU_Id`),
  KEY `G_Id` (`G_Id`),
  KEY `U_Id` (`U_Id`),
  CONSTRAINT `members_ibfk_1` FOREIGN KEY (`G_Id`) REFERENCES `GROUPS` (`G_Id`),
  CONSTRAINT `members_ibfk_2` FOREIGN KEY (`U_Id`) REFERENCES `USERS` (`U_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TRANSACTIONS`
--

DROP TABLE IF EXISTS `TRANSACTIONS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRANSACTIONS` (
  `T_Id` int(11) NOT NULL AUTO_INCREMENT,
  `Sender_Id` int(11) NOT NULL,
  `Receiver_Id` int(11) NOT NULL,
  `Category` varchar(255) NOT NULL,
  `Memo` varchar(255) NOT NULL,
  `Amount` decimal(15,2) NOT NULL,
  `Date` datetime NOT NULL,
  PRIMARY KEY (`T_Id`),
  UNIQUE KEY `T_Id_UNIQUE` (`T_Id`),
  KEY `Sender_Id` (`Sender_Id`),
  KEY `Receiver_Id` (`Receiver_Id`),
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`Sender_Id`) REFERENCES `USERS` (`U_Id`),
  CONSTRAINT `transactions_ibfk_2` FOREIGN KEY (`Receiver_Id`) REFERENCES `USERS` (`U_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `USERS`
--

DROP TABLE IF EXISTS `USERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `USERS` (
  `U_Id` int(11) NOT NULL AUTO_INCREMENT,
  `U_Name` varchar(255) NOT NULL,
  `Email` varchar(255) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `Vericode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`U_Id`),
  UNIQUE KEY `U_Id_UNIQUE` (`U_Id`),
  UNIQUE KEY `Email_UNIQUE` (`Email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-04-08  6:56:43