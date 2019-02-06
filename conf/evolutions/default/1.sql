# Initial schema

# --- !Ups

CREATE TABLE "users" (
  "id" UUID NOT NULL PRIMARY KEY,
  "first_name" varchar NOT NULL,
  "last_name" varchar NOT NULL,
  "user_access_provider_id" UUID NOT NULL,
  "creator_subject_id" UUID,
  "last_modifier_subject_id" UUID,
  "creation_instant" timestamp NOT NULL,
  "update_instant" timestamp NOT NULL
);
CREATE TABLE "users_groups" (
  "id" UUID NOT NULL PRIMARY KEY,
  "name" varchar NOT NULL,
  "user_access_provider_id" UUID NOT NULL,
  "creator_subject_id" UUID,
  "last_modifier_subject_id" UUID,
  "creation_instant" timestamp NOT NULL,
  "update_instant" timestamp NOT NULL
);
CREATE TABLE "users_groups_user" (
  "id" UUID NOT NULL PRIMARY KEY,
  "user_id" UUID NOT NULL,
  "users_group_id" UUID NOT NULL,
  "creator_subject_id" UUID,
  "last_modifier_subject_id" UUID,
  "creation_instant" timestamp NOT NULL,
  "update_instant" timestamp NOT NULL
);
CREATE TABLE "users_access_providers" (
  "id" UUID NOT NULL PRIMARY KEY
);
CREATE TABLE "keys" (
  "id" UUID NOT NULL PRIMARY KEY,
  "value" varchar NOT NULL UNIQUE,
  "owner_user_id" UUID NOT NULL,
  "creator_subject_id" UUID,
  "last_modifier_subject_id" UUID,
  "creation_instant" timestamp NOT NULL,
  "update_instant" timestamp NOT NULL
);
CREATE TABLE "machines" (
  "id" UUID NOT NULL PRIMARY KEY,
  "name" varchar NOT NULL,
  "hostname" varchar NOT NULL,
  "machine_access_provider_id" UUID NOT NULL,
  "creator_subject_id" UUID,
  "last_modifier_subject_id" UUID,
  "creation_instant" timestamp NOT NULL,
  "update_instant" timestamp NOT NULL
);
CREATE TABLE "machines_groups" (
  "id" UUID NOT NULL PRIMARY KEY,
  "name" varchar NOT NULL,
  "machine_access_provider_id" UUID NOT NULL,
  "creator_subject_id" UUID,
  "last_modifier_subject_id" UUID,
  "creation_instant" timestamp NOT NULL,
  "update_instant" timestamp NOT NULL
);
CREATE TABLE "machines_groups_machines" (
  "id" UUID NOT NULL PRIMARY KEY,
  "machine_id" UUID NOT NULL,
  "machines_group_id" UUID NOT NULL,
  "creator_subject_id" UUID,
  "last_modifier_subject_id" UUID,
  "creation_instant" timestamp NOT NULL,
  "update_instant" timestamp NOT NULL
);
CREATE TABLE "machines_access_providers" (
  "id" UUID NOT NULL PRIMARY KEY
);
CREATE TABLE "access_providers" (
  "id" UUID NOT NULL PRIMARY KEY,
  "user_access_provider_id" UUID NOT NULL,
  "machine_access_provider_id" UUID NOT NULL,
  "start_instant" timestamp NOT NULL,
  "end_instant" timestamp NOT NULL,
  "creator_subject_id" UUID,
  "last_modifier_subject_id" UUID,
  "creation_instant" timestamp NOT NULL,
  "update_instant" timestamp NOT NULL
)


# --- !Downs

DROP TABLE "users" cascade;
DROP TABLE "users_groups" cascade;
DROP TABLE "users_groups_user" cascade;
DROP TABLE "users_access_providers" cascade;
DROP TABLE "keys" cascade;
DROP TABLE "machines" cascade;
DROP TABLE "machines_groups" cascade;
DROP TABLE "machines_groups_machines" cascade;
DROP TABLE "machines_access_providers" cascade;
DROP TABLE "access_providers" cascade;
