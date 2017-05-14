-- setup uuid extension first
CREATE SCHEMA IF NOT EXISTS "uuid";
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA uuid;

CREATE TABLE roles (
  id          INT     NOT NULL CONSTRAINT "PK__roles" PRIMARY KEY,
  name        TEXT    NOT NULL CONSTRAINT "UQ__roles__name" UNIQUE,
  permissions TEXT [] NOT NULL
);

CREATE TABLE users (
  id       UUID        NOT NULL CONSTRAINT "PK__users" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  username TEXT        NOT NULL CONSTRAINT "UQ__users__username" UNIQUE,
  email    TEXT        NOT NULL,
  password TEXT        NOT NULL,
  created  TIMESTAMPTZ NOT NULL                                    DEFAULT NOW()
);

CREATE TABLE bans (
  id      BIGSERIAL   NOT NULL CONSTRAINT "PK__bans" PRIMARY KEY,
  userId  UUID        NOT NULL CONSTRAINT "FK__bans__users__userId" REFERENCES users (id),
  author  UUID        NOT NULL CONSTRAINT "FK__bans__users__author" REFERENCES users (id),
  reason  TEXT        NOT NULL,
  created TIMESTAMPTZ NOT NULL,
  expires TIMESTAMPTZ NOT NULL
);

CREATE TABLE user_roles (
  userId UUID NOT NULL CONSTRAINT "FK__user_roles__users__userId" REFERENCES users (id),
  roleId INT  NOT NULL CONSTRAINT "FK__user_roles__roles__roleId" REFERENCES roles (id)
);

CREATE TABLE scenarios (
  id          BIGSERIAL   NOT NULL CONSTRAINT "PK__scenarios" PRIMARY KEY,
  name        TEXT        NOT NULL CONSTRAINT "UQ__scenarios__name" UNIQUE,
  description TEXT        NOT NULL,
  created     TIMESTAMPTZ NOT NULL,
  modified    TIMESTAMPTZ NOT NULL,
  deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
  owner       UUID        NOT NULL CONSTRAINT "FK__scenarios__users__owner" REFERENCES users (id)
);

CREATE TABLE networks (
  id          BIGSERIAL   NOT NULL CONSTRAINT "PK__networks" PRIMARY KEY,
  name        TEXT        NOT NULL CONSTRAINT "UQ__networks__name" UNIQUE,
  tag         TEXT        NOT NULL CONSTRAINT "UQ__networks__tag" UNIQUE,
  description TEXT        NOT NULL,
  created     TIMESTAMPTZ NOT NULL,
  modified    TIMESTAMPTZ NOT NULL,
  deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
  owner       UUID        NOT NULL CONSTRAINT "FK__networks__users__owner" REFERENCES users (id)
);

CREATE TABLE network_permissions (
  networkId BIGINT  NOT NULL CONSTRAINT "FK__network_permissions__networks__networkId" REFERENCES networks (id),
  userId    UUID    NOT NULL CONSTRAINT "FK__network_permissions__users__userId" REFERENCES users (id),
  isAdmin   BOOLEAN NOT NULL,
  CONSTRAINT UQ__network_permissions__networkId__userId UNIQUE (networkId, userId)
);

CREATE TABLE regions (
  id    INT     NOT NULL CONSTRAINT "PK__regions" PRIMARY KEY,
  short CHAR(2) NOT NULL CONSTRAINT "UQ__regions__short" UNIQUE,
  long  TEXT    NOT NULL CONSTRAINT "UQ__regions__long" UNIQUE
);

CREATE TABLE servers (
  id        BIGSERIAL   NOT NULL CONSTRAINT "PK__servers" PRIMARY KEY,
  owner     UUID        NOT NULL CONSTRAINT "FK__servers__users__owner" REFERENCES users (id),
  networkId BIGINT CONSTRAINT "FK__servers_networks__networkId" REFERENCES networks (id),
  name      TEXT        NOT NULL CONSTRAINT "UQ__servers__name" UNIQUE,
  address   TEXT,
  ip        INET        NOT NULL,
  port      INT,
  location  TEXT        NOT NULL,
  region    INT         NOT NULL CONSTRAINT "FK__servers__regions__region" REFERENCES regions (id),
  created   TIMESTAMPTZ NOT NULL,
  modified  TIMESTAMPTZ NOT NULL,
  deleted   BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE TABLE versions (
  id   SERIAL NOT NULL CONSTRAINT "PK__versions" PRIMARY KEY,
  name TEXT   NOT NULL CONSTRAINT "UQ__versions__name" UNIQUE
);

CREATE TABLE styles (
  id           INT     NOT NULL CONSTRAINT "PK__styles" PRIMARY KEY,
  shortName    TEXT    NOT NULL,
  fullName     TEXT    NOT NULL,
  description  TEXT    NOT NULL,
  requiresSize BOOLEAN NOT NULL
);

CREATE TABLE matches (
  id        BIGSERIAL   NOT NULL CONSTRAINT "PK__match" PRIMARY KEY,
  host      UUID        NOT NULL CONSTRAINT "FK__matches__users__host" REFERENCES users (id),
  serverId  BIGINT      NOT NULL CONSTRAINT "FK__matches__servers__serverId" REFERENCES servers (id),
  versionId INT         NOT NULL CONSTRAINT "FK__matches__version__versionId" REFERENCES versions (id),
  styleId   INT         NOT NULL CONSTRAINT "FK__matches__styles__styleId" REFERENCES styles (id),
  size      INT,
  created   TIMESTAMPTZ NOT NULL,
  modified  TIMESTAMPTZ NOT NULL,
  deleted   BOOLEAN     NOT NULL DEFAULT FALSE,
  starts    TIMESTAMPTZ NOT NULL
);

CREATE TABLE match_scenarios (
  matchId    BIGINT NOT NULL CONSTRAINT "FK__match_scenarios__matches__matchId" REFERENCES matches (id),
  scenarioId BIGINT NOT NULL CONSTRAINT "FK__match_scenarios__scenarios__scenarioId" REFERENCES scenarios (id),
  CONSTRAINT "PK__match_scenarios" PRIMARY KEY (matchId, scenarioId)
);

----------
-- DATA --
----------

INSERT INTO regions (id, short, long) VALUES
  (1, 'NA', 'North America'),
  (2, 'EU', 'Europe'),
  (3, 'OC', 'Oceania'),
  (4, 'AS', 'Asia'),
  (5, 'SA', 'South America'),
  (6, 'AF', 'Africa');

INSERT INTO roles (id, name, permissions) VALUES
  (1, 'default', '{"login", "scenario.create", "scenario.edit.own", "scenario.delete.own", "accounts.edit.own"}'),
  (2, 'host',
   '{"network.create", "network.edit.own", "network.delete.own", "network.hosts.add.own", "network.hosts.remove.own", "network.owners.add.own", "network.owners.remove.own", "network.servers.add.own", "network.servers.remove.own", "servers.create", "servers.edit.own", "servers.delete.own", "matches.create", "matches.edit.own", "matches.delete.own"}'),
  (3, 'mod',
   '{"network.edit", "network.delete", "network.hosts.add", "network.hosts.remove", "network.owners.add", "network.owners.remove", "network.servers.add", "network.servers.remove", "servers.edit", "servers.delete", "matches.edit", "matches.delete", "accounts.edit", "accounts.ban", "accounts.roles", "scenario.edit", "scenario.delete"}'),
  (4, 'admin', '{"versions", "styles"}');

INSERT INTO styles (id, shortName, fullName, requiresSize, description) VALUES
  (1, 'FFA', 'Free for All', FALSE, 'TODO'), -- TODO
  (2, 'cTo{{size}}', 'Chosen teams of {{size}}', TRUE, 'TODO'),
  (3, 'rTo{{size}}', 'Random teams of {{size}}', TRUE, 'TODO'),
  (4, 'CptTo{{size}}', 'Captains teams of {{size}}', TRUE, 'TODO'),
  (5, 'pTo{{size}}', 'Picked teams of {{size}}', TRUE, 'TODO'),
  (6, 'Market', 'Slave market', FALSE, 'TODO'),
  (7, 'mTo{{size}}', 'Mystery teams of {{size}}', TRUE, 'TODO'),
  (8, 'RvB', 'Red vs Blue', FALSE, 'TODO');