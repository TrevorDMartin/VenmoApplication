# VenmoApplication
## A Money Transfer API  

This was an end of module, pair programming, mini-capstone.  
We were given the task of completing this Venmo style API called Tenmo.  
In the subdirectory you can view the User Cases.  

We were supplied with the basic structure for a command line client. The majority of the work on the client side was building three classes in the services package. AccountService.java, TransferService.java, and UserService.java to make requests and handle the responses. The rest was finishing methods in the app to print the data.

On the server side we were supplied with a database and the security portion of the api.  
We implemented all security validations in the controllers.   
The server follows MVC design patterns, implements a RESTful API using Spring Boot, and uses Spring JDBC to access and update a PostgreSQL database following the DAO design pattern.  

We built three controllers, three DAO interfaces, and three JdbcDao classes respectively. One each for Account, User, and Transfer.  

[Client Files](venmo/client/src/main/java/com/techelevator/tenmo/)      
  
[Server Files](venmo/server/src/main/java/com/techelevator/tenmo/)  

Hope you enjoy.