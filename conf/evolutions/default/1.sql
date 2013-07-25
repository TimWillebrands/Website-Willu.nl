# --- !Ups

create table piece (
  id                        bigint not null auto_increment,
  name                      varchar(255),
  description               varchar(255),
  kind                      varchar(255),
  addeddate                 varchar(255),
  thumbnail                 varchar(255)
);

create table piece_image (
  id                        bigint not null auto_increment,
  name                      varchar(255),
  description               varchar(255),
  focus                     varchar(255),
  url                       varchar(255)
);



# --- !Downs

drop table if exists piece;
drop table if exists piece_image;
