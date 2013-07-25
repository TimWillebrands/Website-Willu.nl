# --- !Ups

create table piece (
  id                        bigint not null auto_increment,
  name                      varchar(255),
  description               varchar(255),
  kind                      varchar(255),
  addeddate                 varchar(255),
  thumbnail                 varchar(255),
  constraint pk_piece primary key (id)
) engine=innodb
;

create table piece_image (
  id                        bigint not null auto_increment,
  name                      varchar(255),
  description               varchar(255),
  focus                     varchar(255),
  url                       varchar(255),
  constraint pk_piece_image primary key (id)
) engine=innodb
;


alter table piece_image add constraint fk_piece_image_piece_1 foreign key (piece_id) references piece (id) on delete restrict on update restrict;
create index ix_piece_image_piece_1 on piece_image (piece_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS = 0;

drop table if exists piece;

drop table if exists piece_image;

SET FOREIGN_KEY_CHECKS = 1;

drop sequence if exists piece_seq;

drop sequence if exists piece_image_seq;

