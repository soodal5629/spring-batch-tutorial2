CREATE TABLE customer (
    id SERIAL NOT NULL,
    firstName varchar(255) default NULL,
    lastName varchar(255) default NULL,
    birthdate varchar(255) default NULL,
    PRIMARY KEY (id)
);
CREATE TABLE customer2 (
      id SERIAL NOT NULL,
      firstName varchar(255) default NULL,
      lastName varchar(255) default NULL,
      birthdate varchar(255) default NULL,
      PRIMARY KEY (id)
);