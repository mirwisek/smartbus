-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 11, 2021 at 11:31 AM
-- Server version: 10.4.20-MariaDB
-- PHP Version: 7.3.29

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `smart_bus`
--
DROP DATABASE IF EXISTS `smart_bus`;
CREATE DATABASE `smart_bus`;
USE `smart_bus`;

-- --------------------------------------------------------

--
-- Table structure for table `registration`
--

CREATE TABLE `registration` (
  `email` varchar(30) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  `usertype` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `registration`
--

INSERT INTO `registration` (`email`, `username`, `password`, `usertype`) VALUES
('1@gmail.com', 'Asmatullah', 'driver123', 'D'),
('2@gmail.com', 'Asmatullah', 'driver123', 'D'),
('3@gmail.com', 'Jahanzaib', 'driver123', 'D'),
('4@gmail.com', 'Bakhsu', 'driver123', 'D'),
('5@gmail.com', 'Nizam', 'driver123', 'D'),
('admin@gmail.com', 'Admin', 'admin123', 'A'),
('k@gmail.com', 'Khan', 'khan123', 'S');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `registration`
--
ALTER TABLE `registration`
  ADD PRIMARY KEY (`email`);
COMMIT;


CREATE TABLE `buses` (
  `email` varchar(30) NOT NULL,
  `isonline` tinyint(1) NOT NULL DEFAULT 0,
  `currentloc` varchar(100) DEFAULT NULL,
  `lastloc` varchar(100) DEFAULT NULL,
  `busno` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `buses` (`email`, `isonline`, `currentloc`, `lastloc`, `busno`) VALUES
('1@gmail.com', 0, NULL, '30.1774617,67.009527', 'ACB-3121'),
('2@gmail.com', 1, '30.1894945,66.9980787', '30.18971,66.9982123', 'ACB-3121'),
('3@gmail.com', 0, NULL, '30.2005474,66.9963533', 'BCD-1231'),
('4@gmail.com', 0, NULL, '30.194871915854772,67.01767067546771', 'EFG-4321'),
('5@gmail.com', 0, NULL, '30.179033685315133,67.0145714444933', 'HIJ-9999');

ALTER TABLE `buses`
  ADD PRIMARY KEY (`email`);
COMMIT;