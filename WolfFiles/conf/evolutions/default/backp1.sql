# --- !Ups

create table piece (
  id                        bigint NOT NULL AUTO_INCREMENT,
  name                      varchar(255) NOT NULL,
  description               varchar(255) NOT NULL,
  kind                      varchar(255) NOT NULL,
  addeddate                 varchar(255) NOT NULL,
  thumbnail                 varchar(255) NOT NULL,
  constraint pk_piece primary key (id))
;

create table piece_image (
  id                        bigint NOT NULL AUTO_INCREMENT,
  name                      varchar(255) NOT NULL,
  description               varchar(255) NOT NULL,
  focus                     varchar(255) NOT NULL,
  url                       varchar(255) NOT NULL,
  constraint pk_piece_image primary key (id))
;

create sequence piece_seq;

create sequence piece_image_seq;

alter table piece_image add constraint fk_piece_image_piece_1 foreign key (piece_id) references piece (id) on delete restrict on update restrict;
create index ix_piece_image_piece_1 on piece_image (piece_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists piece;

drop table if exists piece_image;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists piece_seq;

drop sequence if exists piece_image_seq;

