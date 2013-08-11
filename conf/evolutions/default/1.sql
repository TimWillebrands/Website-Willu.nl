# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table piece (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  kind                      varchar(255),
  addeddate                 varchar(255),
  thumbnail                 varchar(255),
  constraint pk_piece primary key (id))
;

create table piece_image (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  focus                     varchar(255),
  url                       varchar(255),
  piece_id                  bigint,
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

