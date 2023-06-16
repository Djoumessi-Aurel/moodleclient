-- --------------------------------------------------------
-- Hôte:                         127.0.0.1
-- Version du serveur:           8.0.31 - MySQL Community Server - GPL
-- SE du serveur:                Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------


-- Listage de la structure de la base pour moodleclientdb
DROP DATABASE IF EXISTS `moodleclientdb`;
CREATE DATABASE IF NOT EXISTS `moodleclientdb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `moodleclientdb`;


-- Listage de la structure de table moodleclientdb. cours
DROP TABLE IF EXISTS `cours`;
CREATE TABLE IF NOT EXISTS `cours` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(255) DEFAULT NULL,
  `nom_abrege` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `remoteId` varchar(255) NOT NULL,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `synced` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- Listage de la structure de table moodleclientdb. sections
DROP TABLE IF EXISTS `sections`;
CREATE TABLE IF NOT EXISTS `sections` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(255) DEFAULT NULL,
  `courId` int DEFAULT NULL,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `remote_id` int DEFAULT NULL,
  `synced` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `courId` (`courId`),
  CONSTRAINT `sections_ibfk_1` FOREIGN KEY (`courId`) REFERENCES `cours` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- Listage de la structure de table moodleclientdb. course_file
DROP TABLE IF EXISTS `course_file`;
CREATE TABLE IF NOT EXISTS `course_file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) NOT NULL,
  `hash_name` varchar(255) DEFAULT NULL,
  `sectionId` int DEFAULT NULL,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `synced` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash_name_UNIQUE` (`hash_name`),
  KEY `sectionId` (`sectionId`),
  CONSTRAINT `fk_course_file_1` FOREIGN KEY (`sectionId`) REFERENCES `sections` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- Listage de la structure de table moodleclientdb. devoirs
DROP TABLE IF EXISTS `devoirs`;
CREATE TABLE IF NOT EXISTS `devoirs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `enonce` varchar(255) DEFAULT NULL,
  `date_limite` datetime DEFAULT NULL,
  `coursId` int DEFAULT NULL,
  `etat` varchar(30) DEFAULT 'Non rendu',
  `remoteId` varchar(255) NOT NULL,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `note` smallint DEFAULT NULL,
  `noteMax` smallint DEFAULT NULL,
  `noteVue` tinyint DEFAULT NULL,
  `ignored` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `coursId` (`coursId`),
  CONSTRAINT `devoirs_ibfk_1` FOREIGN KEY (`coursId`) REFERENCES `cours` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- Listage de la structure de table moodleclientdb. ressource_devoir
DROP TABLE IF EXISTS `ressource_devoir`;
CREATE TABLE IF NOT EXISTS `ressource_devoir` (
  `id` int NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) NOT NULL,
  `hash_name` varchar(255) DEFAULT NULL,
  `devoirId` int DEFAULT NULL,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash_name_UNIQUE` (`hash_name`),
  KEY `devoirId` (`devoirId`),
  CONSTRAINT `fk_ressource_devoir_1` FOREIGN KEY (`devoirId`) REFERENCES `devoirs` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- Listage de la structure de table moodleclientdb. assignment_submission
DROP TABLE IF EXISTS `assignment_submission`;
CREATE TABLE IF NOT EXISTS `assignment_submission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) NOT NULL,
  `hash_name` varchar(255) DEFAULT NULL,
  `devoirId` int DEFAULT NULL,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `synced` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash_name_UNIQUE` (`hash_name`),
  KEY `devoirId` (`devoirId`),
  CONSTRAINT `fk_assignment_submission_1` FOREIGN KEY (`devoirId`) REFERENCES `devoirs` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- Listage de la structure de table moodleclientdb. private_file
DROP TABLE IF EXISTS `private_file`;
CREATE TABLE IF NOT EXISTS `private_file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) NOT NULL,
  `hash_name` varchar(255) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `synced` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash_name_UNIQUE` (`hash_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- Listage de la structure de table moodleclientdb. users
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `createdAt` datetime NOT NULL,
  `updatedAt` datetime NOT NULL,
  `server_url` varchar(255) DEFAULT NULL,
  `is_sudent` tinyint DEFAULT NULL,
  `remoteId` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

