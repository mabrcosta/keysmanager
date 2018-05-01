create table "PUBLIC"."keys" (
    "uid" UUID NOT NULL PRIMARY KEY,
    "value" VARCHAR NOT NULL,
    "uid_owner_user" UUID NOT NULL,
    "uid_creator_user" UUID,
    "uid_last_modifier_user" UUID,
    "creation_instant" TIMESTAMP NOT NULL,
    "update_instant" TIMESTAMP NOT NULL);