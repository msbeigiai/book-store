## A book store application implemented in Play framework and Akka actors, MySQL and *CRUD* functionality.
### A microservice approach that uses Akka as data manipulation and play for handling requests.

to run this application simply run:
```bash
sbt
```
to go to *sbt cli* and then:
```bash
run
```
Go to `http://localhost:9000` to see the application.

Go to other *urls* to check app:

```
GET     /books                                  
GET     /books/create                           
GET     /books/:id                              
GET     /books/edit/:id                         
POST    /books/edit                             
POST    /books/create                           
GET     /books/delete/:id  
```

Other functionalities of the application in under implementation ...

