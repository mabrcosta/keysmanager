create table keys (
  uid uuid not null,
  value varchar(255) not null,
  uid_owner_subject uuid not null,
  creation_timestamp timestamp without time zone not null,
  update_timestamp timestamp without time zone not null,
  uid_creator_subject uuid,
  uid_last_modifier_subject uuid,
  constraint pk_keys primary key (uid)
);