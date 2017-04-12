CREATE SCHEMA IF NOT EXISTS uuid;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA uuid;

CREATE TABLE "Account" (
  "Id"                    UUID DEFAULT uuid.uuid_generate_v4() NOT NULL CONSTRAINT "PK_Account" PRIMARY KEY,
  "Name"                  VARCHAR                              NOT NULL CONSTRAINT "UQ_Account_Name" UNIQUE,
  "Email"                 VARCHAR                              NOT NULL CONSTRAINT "UQ_Account_Email" UNIQUE,
  "EmailVerificationCode" UUID,
  "MustResetPassword"     BOOLEAN DEFAULT FALSE                NOT NULL,
  "Continent"             VARCHAR                              NOT NULL,
  "Avatar"                VARCHAR                              NOT NULL,
  "Created"               TIMESTAMPTZ                          NOT NULL,
  "Modified"              TIMESTAMPTZ                          NOT NULL
);
CREATE TABLE "PasswordResetCode" (
  "AccountId" UUID        NOT NULL CONSTRAINT "PK_PasswordResetCode" PRIMARY KEY CONSTRAINT "FK_PasswordResetCode_Account_AccountId" REFERENCES "Account" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "Code"      UUID        NOT NULL,
  "Expires"   TIMESTAMPTZ NOT NULL,
  "Completed" BOOLEAN     NOT NULL
);
CREATE TABLE "Ban" (
  "Id"          BIGSERIAL   NOT NULL CONSTRAINT "PK_Ban" PRIMARY KEY,
  "Reason"      VARCHAR     NOT NULL,
  "Begins"      TIMESTAMPTZ NOT NULL,
  "Ends"        TIMESTAMPTZ NOT NULL,
  "CreatedById" UUID        NOT NULL CONSTRAINT "FK_Ban_Account_CreatedById" REFERENCES "Account" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE "AccountBan" (
  "AccountId" UUID   NOT NULL CONSTRAINT "FK_AccountBan_Account_AccountId" REFERENCES "Account" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "BanId"     BIGINT NOT NULL CONSTRAINT "FK_AccountBan_Ban_BanId" REFERENCES "Ban" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "PK_AccountBan" PRIMARY KEY ("AccountId", "BanId")
);
CREATE TABLE "AccountIp" (
  "Id"        BIGSERIAL   NOT NULL CONSTRAINT "PK_AccountIp" PRIMARY KEY,
  "AccountId" UUID        NOT NULL CONSTRAINT "FK_AccountIp_Account_AccountId" REFERENCES "Account" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "Ip"        INET        NOT NULL,
  "Logged"    TIMESTAMPTZ NOT NULL
);
CREATE TABLE "Server" (
  "Id"          BIGSERIAL NOT NULL CONSTRAINT "PK_Server" PRIMARY KEY,
  "Name"        VARCHAR   NOT NULL,
  "Description" VARCHAR   NOT NULL,
  "Address"     VARCHAR,
  "Ip"          INET      NOT NULL,
  "Port"        INTEGER   NOT NULL,
  "Location"    VARCHAR   NOT NULL,
  "Continent"   VARCHAR   NOT NULL,
  "OwnerId"     UUID      NOT NULL CONSTRAINT "FK_Server_Account_OwnerId" REFERENCES "Account" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE "ServerBan" (
  "ServerId" BIGINT NOT NULL CONSTRAINT "FK_ServerBan_Server_ServerId" REFERENCES "Server" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "BanId"    BIGINT NOT NULL CONSTRAINT "FK_ServerBan_Ban_BanId" REFERENCES "Ban" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "PK_ServerBan" PRIMARY KEY ("ServerId", "BanId")
);
CREATE TABLE "ServerPermission" (
  "ServerId"  BIGINT  NOT NULL CONSTRAINT "FK_ServerPermission_Server_ServerId" REFERENCES "Server" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "AccountId" UUID    NOT NULL CONSTRAINT "FK_ServerPermission_Account_AccountId" REFERENCES "Account" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "Host"      BOOLEAN NOT NULL,
  "Edit"      BOOLEAN NOT NULL,
  CONSTRAINT "PK_ServerPermission" PRIMARY KEY ("ServerId", "AccountId")
);
CREATE TABLE "Mod" (
  "Id"          BIGSERIAL NOT NULL CONSTRAINT "PK_Mod" PRIMARY KEY,
  "Name"        VARCHAR   NOT NULL,
  "Description" VARCHAR   NOT NULL,
  "OwnerId"     UUID      NOT NULL CONSTRAINT "FK_Mod_Account_OwnerId" REFERENCES "Account" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE "Post" (
  "Id"       BIGSERIAL   NOT NULL  CONSTRAINT "PK_Post" PRIMARY KEY ,
  "Name"     VARCHAR     NOT NULL,
  "Opens"    TIMESTAMPTZ NOT NULL,
  "HostId"   UUID        NOT NULL CONSTRAINT "FK_Post_Account_HostId" REFERENCES "Account" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "ServerId" BIGINT      NOT NULL CONSTRAINT "FK_Post_Server_ServerId" REFERENCES "Server" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE "PostRemoval" (
  "PostId"      BIGINT      NOT NULL CONSTRAINT "PK_PostRemoval" PRIMARY KEY CONSTRAINT "FK_PostRemoval_Post_PostId" REFERENCES "Post" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "RemovedById" UUID        NOT NULL CONSTRAINT "FK_PostRemoval_Account_AccountId" REFERENCES "Account" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "Reason"      VARCHAR     NOT NULL,
  "Time"        TIMESTAMPTZ NOT NULL
);
CREATE TABLE "PostMod" (
  "PostId" BIGINT NOT NULL CONSTRAINT "FK_PostMod_Post_PostId" REFERENCES "Post" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "ModId"  BIGINT NOT NULL CONSTRAINT "FK_PostMod_Mod_ModId" REFERENCES "Mod" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "PK_PostMod" PRIMARY KEY ("PostId", "ModId")
);
CREATE TABLE "File" (
  "Id"      UUID DEFAULT uuid.uuid_generate_v4() NOT NULL CONSTRAINT "PK_File" PRIMARY KEY,
  "Path"    VARCHAR                              NOT NULL,
  "Name"    VARCHAR                              NOT NULL,
  "OwnerId" UUID                                 NOT NULL CONSTRAINT "FK_File_Account_OwnerId" REFERENCES "Account" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE "FileApproval" (
  "FileId"       UUID        NOT NULL CONSTRAINT "PK_FileApproval" PRIMARY KEY CONSTRAINT "FK_FileApproval_File_FileId" REFERENCES "File" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "ApprovedById" UUID        NOT NULL CONSTRAINT "FK_FileApproval_Account_ApprovedById" REFERENCES "Account" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "ApprovedAt"   TIMESTAMPTZ NOT NULL
);
CREATE TABLE "ModFile" (
  "ModId"  BIGINT NOT NULL CONSTRAINT "FK_ModFile_Mod_ModId" REFERENCES "Mod" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "FileId" UUID   NOT NULL CONSTRAINT "FK_ModFile_File_FileId" REFERENCES "File" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "PK_ModFile" PRIMARY KEY ("ModId", "FileId")
);
CREATE TABLE "OauthService" (
  "Id"            UUID DEFAULT uuid.uuid_generate_v4() NOT NULL CONSTRAINT "PK_OauthService" PRIMARY KEY,
  "Name"          VARCHAR                              NOT NULL,
  "OwnerId"       UUID                                 NOT NULL CONSTRAINT "FK_OauthService_Account_OwnerId" REFERENCES "Account" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "AllowedGrants" TEXT ARRAY                           NOT NULL
);
CREATE TABLE "OauthToken" (
  "Id"           BIGSERIAL   NOT NULL CONSTRAINT "PK_OauthToken" PRIMARY KEY,
  "ServiceId"    UUID        NOT NULL CONSTRAINT "FK_OauthToken_OauthService_ServiceId" REFERENCES "OauthService" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "AccountId"    UUID        NOT NULL CONSTRAINT "FK_OauthToken_Account_AccountId" REFERENCES "Account" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "AccessToken"  VARCHAR     NOT NULL,
  "RefreshToken" VARCHAR     NOT NULL,
  "Expires"      TIMESTAMPTZ NOT NULL,
  CONSTRAINT "UQ_OauthToken_AccountId_ServiceId" UNIQUE ("AccountId", "ServiceId")
);
CREATE TABLE "LoginInfo" (
  "Id"          BIGSERIAL NOT NULL CONSTRAINT "PK_LoginInfo" PRIMARY KEY,
  "ProviderId"  VARCHAR   NOT NULL,
  "ProviderKey" VARCHAR   NOT NULL
);
CREATE TABLE "AccountLoginInfo" (
  "AccountId"   UUID   NOT NULL CONSTRAINT "FK_AccountLoginInfo_Account_AccountId" REFERENCES "Account" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "LoginInfoId" BIGINT NOT NULL CONSTRAINT "FK_AccountLoginInfo_LoginInfo_LoginInfoId" REFERENCES "LoginInfo" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "PK_AccountLoginInfo" PRIMARY KEY ("AccountId", "LoginInfoId")
);
CREATE TABLE "OauthServiceLoginInfo" (
  "OauthServiceId" UUID   NOT NULL CONSTRAINT "FK_OauthServiceLoginInfo_OauthService_OauthServiceId" REFERENCES "OauthService" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "LoginInfoId"    BIGINT NOT NULL CONSTRAINT "FK_OauthServiceLoginInfo_LoginInfo_LoginInfoId" REFERENCES "LoginInfo" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "PK_OauthServiceLoginInfo" PRIMARY KEY ("OauthServiceId", "LoginInfoId")
);
CREATE TABLE "PasswordInfo" (
  "LoginInfoId" BIGINT  NOT NULL CONSTRAINT "PK_PasswordInfo_LoginInfoId" PRIMARY KEY CONSTRAINT "FK_PasswordInfo_LoginInfo_LoginInfoId" REFERENCES "LoginInfo" ("Id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  "Hasher"      VARCHAR NOT NULL,
  "Password"    VARCHAR NOT NULL,
  "Salt"        VARCHAR
);