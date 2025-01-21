-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: electronic_home_manager
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `apartment_ownership`
--

DROP TABLE IF EXISTS `apartment_ownership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `apartment_ownership` (
  `ownership_id` int NOT NULL AUTO_INCREMENT,
  `apartment_id` int NOT NULL,
  `owner_id` int NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  PRIMARY KEY (`ownership_id`),
  KEY `apartment_id` (`apartment_id`),
  KEY `owner_id` (`owner_id`),
  CONSTRAINT `apartment_ownership_ibfk_1` FOREIGN KEY (`apartment_id`) REFERENCES `apartments` (`apartment_id`),
  CONSTRAINT `apartment_ownership_ibfk_2` FOREIGN KEY (`owner_id`) REFERENCES `residents` (`resident_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apartment_ownership`
--

LOCK TABLES `apartment_ownership` WRITE;
/*!40000 ALTER TABLE `apartment_ownership` DISABLE KEYS */;
INSERT INTO `apartment_ownership` VALUES (3,2,7,'2023-01-01','2025-01-20'),(4,3,7,'2025-01-01',NULL);
/*!40000 ALTER TABLE `apartment_ownership` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `apartments`
--

DROP TABLE IF EXISTS `apartments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `apartments` (
  `apartment_id` int NOT NULL AUTO_INCREMENT,
  `building_id` int DEFAULT NULL,
  `apartment_number` varchar(10) NOT NULL,
  `floor_number` int NOT NULL,
  `area` decimal(10,2) NOT NULL,
  PRIMARY KEY (`apartment_id`),
  KEY `building_id` (`building_id`),
  CONSTRAINT `apartments_ibfk_1` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`building_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apartments`
--

LOCK TABLES `apartments` WRITE;
/*!40000 ALTER TABLE `apartments` DISABLE KEYS */;
INSERT INTO `apartments` VALUES (2,2,'15',10,300.00),(3,4,'1',1,700.00),(4,4,'2',2,650.00),(5,5,'2',1,250.00);
/*!40000 ALTER TABLE `apartments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `buildings`
--

DROP TABLE IF EXISTS `buildings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `buildings` (
  `building_id` int NOT NULL AUTO_INCREMENT,
  `employee_id` int DEFAULT NULL,
  `address` varchar(200) NOT NULL,
  `total_floors` int NOT NULL,
  `total_apartments` int NOT NULL,
  `total_area` decimal(10,2) NOT NULL,
  `has_elevator` tinyint(1) DEFAULT '0',
  `common_area_description` text,
  PRIMARY KEY (`building_id`),
  KEY `employee_id` (`employee_id`),
  CONSTRAINT `buildings_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `buildings`
--

LOCK TABLES `buildings` WRITE;
/*!40000 ALTER TABLE `buildings` DISABLE KEYS */;
INSERT INTO `buildings` VALUES (2,1,'sofia',10,30,1000.00,1,'outside park'),(4,3,'musagenica',3,3,10000.00,0,'golqmo mejdublokovo prostranstvo'),(5,1,'sofia grad',1,1,1000.00,0,'nqma');
/*!40000 ALTER TABLE `buildings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `companies`
--

DROP TABLE IF EXISTS `companies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `companies` (
  `company_id` int NOT NULL AUTO_INCREMENT,
  `company_name` varchar(100) NOT NULL,
  `address` varchar(200) DEFAULT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `total_revenue` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `companies`
--

LOCK TABLES `companies` WRITE;
/*!40000 ALTER TABLE `companies` DISABLE KEYS */;
INSERT INTO `companies` VALUES (1,'erik','sofia','0999203813','eri.skdjm@bv',25300.00),(6,'telus','madrid','0101010101','telus@com',0.00);
/*!40000 ALTER TABLE `companies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employees` (
  `employee_id` int NOT NULL AUTO_INCREMENT,
  `company_id` int DEFAULT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `hire_date` date DEFAULT NULL,
  `status` enum('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
  PRIMARY KEY (`employee_id`),
  KEY `company_id` (`company_id`),
  CONSTRAINT `employees_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `companies` (`company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES (1,1,'John','davidov','1234567894','joe@ok','2023-01-01','ACTIVE'),(2,1,'alga ','solf','0301118777','erik-@sj','2025-01-03','ACTIVE'),(3,1,'erik','antov','1234567890','erik@zbv.bg','2025-01-03','ACTIVE');
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_structure`
--

DROP TABLE IF EXISTS `fee_structure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fee_structure` (
  `fee_id` int NOT NULL AUTO_INCREMENT,
  `building_id` int DEFAULT NULL,
  `base_rate_per_sqm` decimal(10,2) NOT NULL,
  `elevator_fee_per_person` decimal(10,2) DEFAULT '0.00',
  `pet_fee` decimal(10,2) DEFAULT '0.00',
  `effective_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  PRIMARY KEY (`fee_id`),
  KEY `building_id` (`building_id`),
  CONSTRAINT `fee_structure_ibfk_1` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`building_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fee_structure`
--

LOCK TABLES `fee_structure` WRITE;
/*!40000 ALTER TABLE `fee_structure` DISABLE KEYS */;
INSERT INTO `fee_structure` VALUES (8,2,10000.00,10.00,10.00,'2025-09-09','2025-09-08'),(9,2,1000.00,10.00,20.00,'2025-01-08','2025-02-08'),(10,4,5.00,5.00,5.00,'2025-01-08','2025-01-07'),(11,4,10.00,10.00,10.00,'2025-01-19','2025-02-19'),(12,5,25.00,15.00,10.00,'2025-01-21','2025-05-10');
/*!40000 ALTER TABLE `fee_structure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `apartment_id` int DEFAULT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_date` date NOT NULL,
  `payment_month` date NOT NULL,
  `status` enum('PENDING','PAID','OVERDUE') DEFAULT 'PENDING',
  PRIMARY KEY (`payment_id`),
  KEY `apartment_id` (`apartment_id`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`apartment_id`) REFERENCES `apartments` (`apartment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,4,6500.00,'2025-01-19','2025-01-19','PENDING'),(2,5,6250.00,'2025-01-21','2025-01-21','PENDING'),(3,5,6275.00,'2025-01-21','2025-01-21','PENDING'),(4,5,6275.00,'2025-01-21','2025-01-21','OVERDUE');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `residents`
--

DROP TABLE IF EXISTS `residents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `residents` (
  `resident_id` int NOT NULL AUTO_INCREMENT,
  `apartment_id` int DEFAULT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `date_of_birth` date NOT NULL,
  `has_pet` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`resident_id`),
  KEY `apartment_id` (`apartment_id`),
  CONSTRAINT `residents_ibfk_1` FOREIGN KEY (`apartment_id`) REFERENCES `apartments` (`apartment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `residents`
--

LOCK TABLES `residents` WRITE;
/*!40000 ALTER TABLE `residents` DISABLE KEYS */;
INSERT INTO `residents` VALUES (7,3,'John','Doe','1980-01-01',1),(8,2,'Konstantin','Valentinov','2005-08-10',0),(9,2,'Nikola','Vaptsarov','1970-09-09',0),(10,2,'Valentina','Starcheva','2001-06-20',1),(11,4,'kose','bose','2000-05-05',0),(12,5,'anna','delvy','2002-03-03',1);
/*!40000 ALTER TABLE `residents` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-21 12:11:16
