# Demo-Bank

Demo bank application is basic bank application have features like create account, 
fetch account details, make fund transfer, deposit fund and withdraw fund.

### How do I get set up and run the application? ###

* Summary of set up:
  This is a spring boot application. Main class of this application is com.banking.simplebankapp.SimpleBankAppApplication.

* Dependencies:
  This application requires Java 17, Postgresql, Kafka. Install these dependencies using the following links:
    * Java 17: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
    * Postgresql: Version 15. https://www.postgresql.org/download.
    * Kafka: https://kafka.apache.org/downloads

* Run postgres in your local.
* Run zookeeper and kafka server in your local. 
  * Use this command to run zookeeper: ``bin/zookeeper-server-start.sh config/zookeeper.properties``
  * Use this command to run kafka: ``bin/kafka-server-start.sh config/server.properties``

* Database configuration:
  Create a database named "demo_bank".
  Create a database user named "postgres" with password "password"
* Run the Demo Bank springboot application from your favorite code editor.


### Tech Stack Used ###
* Java 17
* SpringBoot
* Kafka
* PostgreSQL
* JUnit