-- setup uuid extension first
CREATE SCHEMA IF NOT EXISTS "uuid";
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA uuid;

CREATE TABLE roles (
  uuid        UUID    NOT NULL CONSTRAINT "PK__roles" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  name        TEXT    NOT NULL CONSTRAINT "UQ__roles__name" UNIQUE,
  permissions TEXT [] NOT NULL
);

CREATE TABLE users (
  uuid     UUID        NOT NULL CONSTRAINT "PK__users" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  username TEXT        NOT NULL CONSTRAINT "UQ__users__username" UNIQUE,
  email    TEXT        NOT NULL,
  password TEXT        NOT NULL,
  created  TIMESTAMPTZ NOT NULL                                    DEFAULT NOW(),
  modified TIMESTAMPTZ NOT NULL                                    DEFAULT NOW()
);

CREATE TABLE bans (
  uuid         UUID        NOT NULL CONSTRAINT "PK__bans" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  bannedUserId UUID        NOT NULL CONSTRAINT "FK__bans__users__bannedUserId" REFERENCES users (uuid),
  authorUserId UUID        NOT NULL CONSTRAINT "FK__bans__users__authorUserId" REFERENCES users (uuid),
  reason       TEXT        NOT NULL,
  created      TIMESTAMPTZ NOT NULL,
  modified     TIMESTAMPTZ NOT NULL,
  expires      TIMESTAMPTZ NOT NULL
);

CREATE TABLE user_roles (
  userId UUID NOT NULL CONSTRAINT "FK__user_roles__users__userId" REFERENCES users (uuid),
  roleId UUID NOT NULL CONSTRAINT "FK__user_roles__roles__roleId" REFERENCES roles (uuid)
);

CREATE TABLE scenarios (
  uuid        UUID        NOT NULL CONSTRAINT "PK__scenarios" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  name        TEXT        NOT NULL CONSTRAINT "UQ__scenarios__name" UNIQUE,
  description TEXT        NOT NULL,
  created     TIMESTAMPTZ NOT NULL,
  modified    TIMESTAMPTZ NOT NULL,
  deleted     BOOLEAN     NOT NULL                                        DEFAULT FALSE,
  ownerUserId UUID        NOT NULL CONSTRAINT "FK__scenarios__users__ownerUserId" REFERENCES users (uuid)
);

CREATE TABLE networks (
  uuid        UUID        NOT NULL CONSTRAINT "PK__networks" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  name        TEXT        NOT NULL CONSTRAINT "UQ__networks__name" UNIQUE,
  tag         TEXT        NOT NULL CONSTRAINT "UQ__networks__tag" UNIQUE,
  description TEXT        NOT NULL,
  created     TIMESTAMPTZ NOT NULL,
  modified    TIMESTAMPTZ NOT NULL,
  deleted     BOOLEAN     NOT NULL                                       DEFAULT FALSE,
  ownerUserId UUID        NOT NULL CONSTRAINT "FK__networks__users__ownerUserId" REFERENCES users (uuid)
);

CREATE TABLE network_permissions (
  networkId UUID    NOT NULL CONSTRAINT "FK__network_permissions__networks__networkId" REFERENCES networks (uuid),
  userId    UUID    NOT NULL CONSTRAINT "FK__network_permissions__users__userId" REFERENCES users (uuid),
  isAdmin   BOOLEAN NOT NULL,
  CONSTRAINT UQ__network_permissions__networkId__userId UNIQUE (networkId, userId)
);

CREATE TABLE regions (
  uuid  UUID    NOT NULL CONSTRAINT "PK__regions" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  short CHAR(2) NOT NULL CONSTRAINT "UQ__regions__short" UNIQUE,
  long  TEXT    NOT NULL CONSTRAINT "UQ__regions__long" UNIQUE
);

CREATE TABLE servers (
  uuid        UUID        NOT NULL CONSTRAINT "PK__servers" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  ownerUserId UUID        NOT NULL CONSTRAINT "FK__servers__users__ownerUserId" REFERENCES users (uuid),
  networkId   UUID        NOT NULL CONSTRAINT "FK__servers_networks__networkId" REFERENCES networks (uuid),
  name        TEXT        NOT NULL CONSTRAINT "UQ__servers__name" UNIQUE,
  address     TEXT,
  ip          INET        NOT NULL,
  port        INT,
  location    TEXT        NOT NULL,
  regionId    UUID        NOT NULL CONSTRAINT "FK__servers__regions__regionId" REFERENCES regions (uuid),
  created     TIMESTAMPTZ NOT NULL,
  modified    TIMESTAMPTZ NOT NULL,
  deleted     BOOLEAN     NOT NULL                                      DEFAULT FALSE
);

CREATE TABLE versions (
  uuid UUID    NOT NULL CONSTRAINT "PK__versions" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  name TEXT    NOT NULL CONSTRAINT "UQ__versions__name" UNIQUE,
  live BOOLEAN NOT NULL                                       DEFAULT TRUE
);

CREATE TABLE styles (
  uuid         UUID    NOT NULL CONSTRAINT "PK__styles" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  shortName    TEXT    NOT NULL,
  fullName     TEXT    NOT NULL,
  description  TEXT    NOT NULL,
  requiresSize BOOLEAN NOT NULL
);

CREATE TABLE matches (
  uuid       UUID        NOT NULL CONSTRAINT "PK__match" PRIMARY KEY DEFAULT uuid.uuid_generate_v4(),
  hostUserId UUID        NOT NULL CONSTRAINT "FK__matches__users__hostUserId" REFERENCES users (uuid),
  serverId   UUID        NOT NULL CONSTRAINT "FK__matches__servers__serverId" REFERENCES servers (uuid),
  versionId  UUID        NOT NULL CONSTRAINT "FK__matches__version__versionId" REFERENCES versions (uuid),
  styleId    UUID        NOT NULL CONSTRAINT "FK__matches__styles__styleId" REFERENCES styles (uuid),
  size       INT,
  created    TIMESTAMPTZ NOT NULL,
  modified   TIMESTAMPTZ NOT NULL,
  deleted    BOOLEAN     NOT NULL                                    DEFAULT FALSE,
  starts     TIMESTAMPTZ NOT NULL
);

CREATE TABLE match_scenarios (
  matchId    UUID NOT NULL CONSTRAINT "FK__match_scenarios__matches__matchId" REFERENCES matches (uuid),
  scenarioId UUID NOT NULL CONSTRAINT "FK__match_scenarios__scenarios__scenarioId" REFERENCES scenarios (uuid),
  CONSTRAINT "PK__match_scenarios" PRIMARY KEY (matchId, scenarioId)
);

----------
-- DATA --
----------

INSERT INTO regions (uuid, short, long) VALUES
  ('00000000-0000-0000-0000-000000000000', 'NA', 'North America'),
  ('00000000-0000-0000-0000-000000000001', 'EU', 'Europe'),
  ('00000000-0000-0000-0000-000000000002', 'OC', 'Oceania'),
  ('00000000-0000-0000-0000-000000000003', 'AS', 'Asia'),
  ('00000000-0000-0000-0000-000000000004', 'SA', 'South America'),
  ('00000000-0000-0000-0000-000000000005', 'AF', 'Africa');

INSERT INTO roles (uuid, name, permissions) VALUES
  ('00000000-0000-0000-0000-000000000000', 'default',
   '{"login", "scenario.create", "scenario.edit.own", "scenario.delete.own", "accounts.edit.own"}'),
  ('00000000-0000-0000-0000-000000000001', 'host',
   '{"network.create", "network.edit.own", "network.delete.own", "network.hosts.add.own", "network.hosts.remove.own", "network.owners.add.own", "network.owners.remove.own", "network.servers.add.own", "network.servers.remove.own", "servers.create", "servers.edit.own", "servers.delete.own", "matches.create", "matches.edit.own", "matches.delete.own"}'),
  ('00000000-0000-0000-0000-000000000002', 'mod',
   '{"network.edit", "network.delete", "network.hosts.add", "network.hosts.remove", "network.owners.add", "network.owners.remove", "network.servers.add", "network.servers.remove", "servers.edit", "servers.delete", "matches.edit", "matches.delete", "accounts.edit", "accounts.ban", "accounts.roles", "scenario.edit", "scenario.delete"}'),
  ('00000000-0000-0000-0000-000000000003', 'admin', '{"versions", "styles"}');

INSERT INTO styles (uuid, shortName, fullName, requiresSize, description) VALUES
  ('00000000-0000-0000-0000-000000000000', 'FFA', 'Free for All', FALSE, 'TODO'), -- TODO
  ('00000000-0000-0000-0000-000000000001', 'cTo{{size}}', 'Chosen teams of {{size}}', TRUE, 'TODO'),
  ('00000000-0000-0000-0000-000000000002', 'rTo{{size}}', 'Random teams of {{size}}', TRUE, 'TODO'),
  ('00000000-0000-0000-0000-000000000003', 'CptTo{{size}}', 'Captains teams of {{size}}', TRUE, 'TODO'),
  ('00000000-0000-0000-0000-000000000004', 'pTo{{size}}', 'Picked teams of {{size}}', TRUE, 'TODO'),
  ('00000000-0000-0000-0000-000000000005', 'Market', 'Slave market', FALSE, 'TODO'),
  ('00000000-0000-0000-0000-000000000006', 'mTo{{size}}', 'Mystery teams of {{size}}', TRUE, 'TODO'),
  ('00000000-0000-0000-0000-000000000007', 'RvB', 'Red vs Blue', FALSE, 'TODO');

INSERT INTO versions (uuid, name, live) VALUES
  ('00000000-0000-0000-0000-000000000000', 'test', TRUE);