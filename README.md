# Smart Ocean Cleaner

## Tool presentation and installation
The tool usage is presented by the following video: https://www.youtube.com/watch?v=MzTk3LZYG8s.

Alternatively, it is possible to run the software code, that has been developed and configured using Eclipse EE, Tomcat and MySQL workbench.
In order to run the code on the local Tomcat server, it is necessary to import the project in Eclipse EE, configure a Tomcat server (for instance, it is possible to use localhost to run it locally, and an adequate port could be 8080 or 8081, so that the tool could be accessed in the browser by typing localhost:8080/smartOceanCleaner/ or localhost:8081/smartOceanCleaner/ respectively).
MySQL Workbench should also be configured by creating a database schema according to the configuration available in web.xml. It is possible to create the database by using the following code:

<details>
<summary>Show the code</summary>

```
CREATE DATABASE `smartCleanerDB` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE `campaign` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `customer` varchar(45) NOT NULL,
  `status` tinyint NOT NULL DEFAULT '0',
  `manager` int NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `user` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `psw` varchar(45) NOT NULL,
  `e-mail` varchar(45) NOT NULL,
  `points` int NOT NULL,
  `isAdmin` tinyint NOT NULL,
  `isRobot` tinyint NOT NULL,
  `percentage` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `e-mail_UNIQUE` (`e-mail`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `vessel` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `latitude` varchar(45) NOT NULL,
  `longitude` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `municipality` varchar(45) NOT NULL,
  `region` varchar(45) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `image` (
  `ID_Image` int NOT NULL AUTO_INCREMENT,
  `ID_Vessel` int NOT NULL,
  `source` varchar(45) NOT NULL,
  `resolution` int NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`ID_Image`),
  KEY `vessel_idx` (`ID_Vessel`),
  CONSTRAINT `vessel` FOREIGN KEY (`ID_Vessel`) REFERENCES `vessel` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `annotation` (
  `ID_Annotation` int NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `trust` tinyint NOT NULL,
  `wasteType` varchar(400) NOT NULL,
  `ID_Image` int NOT NULL,
  `ID_User` int NOT NULL,
  PRIMARY KEY (`ID_Annotation`),
  KEY `image_idx` (`ID_Image`),
  CONSTRAINT `image` FOREIGN KEY (`ID_Image`) REFERENCES `image` (`ID_Image`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `campaign_vessel` (
  `ID_Campaign` int NOT NULL,
  `ID_Vessel` int NOT NULL,
  PRIMARY KEY (`ID_Campaign`,`ID_Vessel`),
  KEY `ID_Vessel_idx` (`ID_Vessel`),
  CONSTRAINT `idcampaign` FOREIGN KEY (`ID_Campaign`) REFERENCES `campaign` (`ID`),
  CONSTRAINT `idvessel` FOREIGN KEY (`ID_Vessel`) REFERENCES `vessel` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



CREATE TABLE `player_campaign` (
  `ID_User` int NOT NULL,
  `ID_Campaign` int NOT NULL,
  PRIMARY KEY (`ID_User`,`ID_Campaign`),
  KEY `ID_Campaign_idx` (`ID_Campaign`),
  CONSTRAINT `ID_Campaign` FOREIGN KEY (`ID_Campaign`) REFERENCES `campaign` (`ID`),
  CONSTRAINT `username_player` FOREIGN KEY (`ID_User`) REFERENCES `user` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


```
</details>
In a real-world scenario, the system could be installed on a central server and accessed by every device connected to the Internet network by typing the web application address (for instance, www.smartoceancleaner.org).


## Screenshots
![](https://raw.githubusercontent.com/nicolopinci/smartOceanCleaner/master/screenshots/Signup.png)
![](https://raw.githubusercontent.com/nicolopinci/smartOceanCleaner/master/screenshots/Login.png)
![](https://raw.githubusercontent.com/nicolopinci/smartOceanCleaner/master/screenshots/PlayerHome.png)
![](https://raw.githubusercontent.com/nicolopinci/smartOceanCleaner/master/screenshots/ManagerHome.png)
![](https://raw.githubusercontent.com/nicolopinci/smartOceanCleaner/master/screenshots/editProfile.png)
![](https://raw.githubusercontent.com/nicolopinci/smartOceanCleaner/master/screenshots/contestDetails.png)
![](https://raw.githubusercontent.com/nicolopinci/smartOceanCleaner/master/screenshots/locationDetails.png)
![](https://raw.githubusercontent.com/nicolopinci/smartOceanCleaner/master/screenshots/mapVessels.png)
![](https://raw.githubusercontent.com/nicolopinci/smartOceanCleaner/master/screenshots/Sort.png)




## The context
With rapid industrialization, waste is becoming one of the most relevant issues to deal with. In particular, plastic has become the most commonly used material in the production of millions of products. Such overuse of plastic in today’s society has become a major environmental threat to oceanic space. Every year almost 8 million tons of plastic is dumped in the oceans, which is equivalent to dumping a whole truck of waste into the oceans every minute. Thus, the aquatic ecosystem, as a whole, is at risk of becoming over-contaminated over the years.

As plastic does not dissolve, it remains in the water, thereby hampering its purity. This gives a clear sign that there will not be much clean water left in the coming years. Plastic pollution has a dangerous impact on marine life to a great extent \cite{science-plastic}. It causes death to marine inhabitants like turtles, dolphins, whales and likewise, simply because marine creatures can become entangled or confuse plastic waste with food. Scientists have called plastic pollution one of the generation’s key environmental issues because of its non-biodegradable nature harming wildlife, aquatic life and ultimately human life. While plastic takes up to 500 years to decompose, some of it could break down into tiny particles, which in turn end up into seafood people consume. 

By 2050, there will be more plastic in the ocean than fish. If no proper measures are taken rapidly, scientists predict that the weight of ocean plastic will exceed the total weight of marine creatures in the seas. This has become a global crisis. Most of the plastic waste ending up in the oceans comes from land, due to limited waste management and if proper recycling facilities are not available, all the plastic ends up as trash. In US, almost 100% of trash is collected but only 30% is being recycled. It ends up in the trash, gets contaminated or else the recycling systems could not handle it. On the other hand, the situation is worse in the developing nations, where proper waste management technologies or systems that look over the problems arising due to plastic waste are absent and the management fails to keep pace with the consumption. Thus, most of the plastic ends up being dumped into nature because of its mismanagement.

As plastic in the ocean space is becoming a serious problem, the project aims at **raising awareness** among the population through the use of **gamification**, whose purpose is also the **improvement of an automatic sorting system** based on artificial intelligence. This would strengthen the marketing of the project, as well as developing an understanding of the severity of the problem and actively engaging the audience in solving a global issue. The goal of this initiative is to prevent an excessive amount of plastic to end up in the ocean.

## The gamification platform

This proposal relies on a web application, which provides the gamification interface. The autonomous vessel is able to collect the waste from the ocean and to analyze it automatically using a computer vision system. 
The web application’s purpose is to allow the user to manually sort the waste so that the data can then be stored in a database and compared with the data from the automatic computer vision system. 

The application allows **two** different **kinds of users**, that are the player and the administrator. At the moment, to allow using the application even **without a real vessel**, the administrator is also able to upload the images, that would be uploaded by the automatic system when the idea will be realized. Moreover, each vessel can be used by a specific customer, for instance, the Norwegian government, and each customer could use several vessels at the same time. The vessels can then be part of **contests**, organized by a specific customer, for instance, if the Norwegian government owns ten vessels, of which five are close to Nordland and five are close to Møre og Romsdal, it could decide to create two contests, one for each area.

A player can **enrol in an open contest** in which he or she has not enrolled before and classify the images. When a contest is selected, the player is able to access a map containing all the vessels related to it. By clicking on a specific vessel, he or she can start sorting the garbage for that vessel, **earning points** on the basis of the estimated precision, that is based on the other users' classification and on the previous precision of that user.

Once a user has classified an image, this information is loaded into the database and, after a few user evaluations, the physical object is sent to a container in the vessel. The database is a **central database**, possibly distributed, where the data from all the vessels are loaded as they are generated. The application also allows the users to perform some account management operations, in order to change their profile information, for instance username, password, e-mail address and profile image.
