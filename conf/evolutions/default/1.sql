# --- !Ups



CREATE TABLE UserData(
                     id INT PRIMARY KEY Auto_Increment,
                     fname varchar(20) ,
                     mname varchar(20),
                     lname varchar(20),
                     email  varchar(40),
                     password varchar(10),
                     mobile varchar(12),
                     gender varchar(8),
                     age INT,
                     hobbies varchar(100),
                     haveAuthToLogin boolean,
                     isNormalUser boolean,
                     );

CREATE TABLE AssignmentData (
                          id int AUTO_INCREMENT PRIMARY KEY,
                          title varchar(100) ,
                          description varchar(100)
                      );


# --- !Downs

DROP TABLE UserData;
DROP TABLE AssignmentData;

