-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 05, 2021 at 12:45 PM
-- Server version: 10.4.13-MariaDB
-- PHP Version: 7.4.7

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

-- --------------------------------------------------------

--
-- Table structure for table `buses`
--

CREATE TABLE `buses` (
  `email` varchar(30) NOT NULL,
  `isonline` tinyint(1) NOT NULL DEFAULT 0,
  `currentloc` varchar(20) DEFAULT NULL,
  `lastloc` varchar(20) DEFAULT NULL,
  `busno` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `buses`
--

INSERT INTO `buses` (`email`, `isonline`, `currentloc`, `lastloc`, `busno`) VALUES
('1@gmail.com', 0, NULL, '30.1774915,67.009490', 'ACB-3121'),
('2@gmail.com', 0, NULL, NULL, 'ACB-3121'),
('3@gmail.com', 0, NULL, NULL, 'BCD-1231'),
('4@gmail.com', 0, NULL, NULL, 'EFG-4321'),
('5@gmail.com', 0, NULL, NULL, 'HIJ-9999'),
('m@gmail.com', 0, NULL, '30.1774738,67.009514', '1212'),
('zain123@gmail.com', 1, '124', '123', 'a123');

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
('zain671997@gmail.com', 'zain123', 'zainzain', 'D'),
('zain@gmial.com', 'zain', '12345', 'S');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `buses`
--
ALTER TABLE `buses`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `registration`
--
ALTER TABLE `registration`
  ADD PRIMARY KEY (`email`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
